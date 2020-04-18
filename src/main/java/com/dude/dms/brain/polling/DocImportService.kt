package com.dude.dms.brain.polling

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.LineContainer
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.LineService
import com.dude.dms.backend.service.WordService
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.brain.parsing.Spellchecker
import com.dude.dms.brain.t
import com.dude.dms.ui.Const
import org.springframework.stereotype.Component
import java.io.File
import java.io.Serializable
import java.time.LocalDateTime

@Component
class DocImportService(
        private val fileManager: FileManager,
        private val docParser: DocParser,
        private val docService: DocService,
        private val lineService: LineService,
        private val wordService: WordService
) {

    data class Filter(
            var tag: Tag? = null,
            var attribute: Attribute? = null,
            var text: String? = null
    ) : Serializable

    private val docs = mutableSetOf<DocContainer>()

    val progress
        get() = _progress

    val progressText
        get() = _progressText

    val count: Int
        get() {
            val count = pdfs.size + imgs.size
            if (count > docs.size && !importing) Thread { import() }.start()
            return count
        }

    private val pdfs
        get() = File(Options.get().doc.pollingPath).listFiles { _, name -> name.endsWith(".pdf") } ?: emptyArray()
    private val imgs
        get() = File(Options.get().doc.pollingPath).listFiles { _, name -> Const.IMAGE_FORMATS.any { name.endsWith(it) } } ?: emptyArray()

    fun import() {
        if (importing) return
        importing = true
        _progress = 0.0
        try {
            val pdfs = File(Options.get().doc.pollingPath).listFiles { _, name -> name.endsWith(".pdf") }
                    ?: emptyArray()
            val imgs = File(Options.get().doc.pollingPath).listFiles { _, name -> Const.IMAGE_FORMATS.any { name.endsWith(it) } }
                    ?: emptyArray()
            val dcs = mutableSetOf<DocContainer>()
            for (pdf in pdfs.filter { pdf -> pdf.name !in docs.mapNotNull { it.file?.name } && pdf.name !in currentImports }) {
                dcs.add(fileManager.importPdf(pdf, false)?.let { guid -> DocContainer(guid, pdf) } ?: continue)
            }
            for (img in imgs.filter { img -> img.name !in docs.mapNotNull { it.file?.name } && img.name !in currentImports }) {
                dcs.add(fileManager.importImage(img, false)?.let { guid -> DocContainer(guid, img) } ?: continue)
            }
            currentImports.addAll(dcs.mapNotNull { it.file?.name })
            val spellcheckers = Const.OCR_LANGUAGES.map { it to Spellchecker(it) }.toMap()
            val size = dcs.size
            val progressMax = size * 4.0
            dcs.forEachIndexed { index, dc ->
                _progressText = "${index + 1} / $size    " + t("pdf.parse")
                dc.pdfLines = docParser.getPdfText(dc.guid).map { LineContainer(it) }.toSet()
                if (dc.pdfLines.isNotEmpty()) {
                    dc.language = spellcheckers.minBy { entry -> dc.pdfLines.flatMap { it.words }.count { entry.value.check(it.word.text) != null } }!!.key
                }
                _progressText += " > ${t("recognized.language")}: ${dc.language}"
                _progress = (index * 4 + 1) / progressMax
                dc.image = fileManager.getFirstImage(dc.guid)
                _progressText += " > " + t("image.ocr", dc.language)
                dc.ocrLines = docParser.getOcrText(dc.image!!, dc.language).map { LineContainer(it) }.toSet()
                _progress = (index * 4 + 2) / progressMax
                dc.pdfLines.flatMap { it.words }.forEach { it.spelling = spellcheckers.getValue(dc.language).check(it.word.text) }
                dc.ocrLines.flatMap { it.words }.forEach { it.spelling = spellcheckers.getValue(dc.language).check(it.word.text) }
                _progress = (index * 4 + 3) / progressMax

                dc.date = docParser.getMostFrequentDate(dc.lineEntities)
                dc.tags = docParser.discoverTags(dc.lineEntities).toMutableSet()

                dc.useOcrTxt = dc.ocrLines.sumBy { it.words.size } > dc.pdfLines.sumBy { it.words.size }
                docs.add(dc)
                currentImports.remove(dc.file?.name)
                _progress = (index * 4 + 4) / progressMax
            }
        } finally {
            _progress = 1.0
            importing = false
        }
    }

    fun create(docContainer: DocContainer) {
        val lines = docContainer.lineEntities
        val doc = Doc(
                docContainer.guid,
                docContainer.date,
                LocalDateTime.now(),
                if (lines.isNotEmpty()) docService.getFullTextMemory(lines) else null,
                docContainer.tags
        )
        docService.create(doc, docContainer.attributeValues)
        lines.forEach { line ->
            line.doc = doc
            lineService.create(line)
            line.words.forEach { word ->
                wordService.create(word)
            }
        }
        docContainer.file?.delete()
        docs.remove(docContainer)
    }

    fun findAll() = docs.toSet()

    fun findByFilter(filter: Filter) = docs.filter { dc ->
        filter.tag?.let { dc.tags.contains(it) } ?: true
                && filter.attribute?.let { attribute -> dc.tags.flatMap { it.attributes }.contains(attribute) } ?: true
                && filter.text?.let { text -> (if (dc.useOcrTxt) dc.ocrLines else dc.pdfLines).flatMap { it.words }.any { text.contains(it.word.text, true) } } ?: true
    }.toSet()

    companion object {
        private var importing = false
        private val currentImports = mutableSetOf<String>()
        private var _progress = 0.0
        private var _progressText = ""
    }
}