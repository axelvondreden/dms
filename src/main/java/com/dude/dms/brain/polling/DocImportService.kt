package com.dude.dms.brain.polling

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.LineContainer
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.brain.parsing.Spellchecker
import com.dude.dms.ui.Const
import org.springframework.stereotype.Component
import java.io.File
import java.io.Serializable

@Component
class DocImportService(private val fileManager: FileManager, private val docParser: DocParser) {

    data class Filter(
            var tag: Tag? = null,
            var attribute: Attribute? = null,
            var text: String? = null
    ) : Serializable

    private val docs = mutableSetOf<DocContainer>()

    val progress
        get() = _progress

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
            val progressMax = dcs.size * 4.0
            dcs.forEachIndexed { index, dc ->
                dc.pdfLines = docParser.getPdfText(dc.guid).map { LineContainer(it) }.toSet()
                if (dc.pdfLines.isNotEmpty()) {
                    dc.language = spellcheckers.minBy { entry -> dc.pdfLines.flatMap { it.words }.sumBy { entry.value.check(it.word.text).size } }!!.key
                }
                _progress = (index * 4 + 1) / progressMax
                dc.ocrLines = docParser.getOcrText(fileManager.getFirstImage(dc.guid), dc.language).map { LineContainer(it) }.toSet()
                _progress = (index * 4 + 2) / progressMax
                dc.pdfLines.flatMap { it.words }.forEach { it.spelling = spellcheckers.getValue(dc.language).check(it.word.text).toSet() }
                dc.ocrLines.flatMap { it.words }.forEach { it.spelling = spellcheckers.getValue(dc.language).check(it.word.text).toSet() }
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

    fun findByFilter(filter: Filter): Set<DocContainer> {
        return docs.filter { dc ->
            filter.tag?.let { dc.tags.contains(it) } ?: true
                    && filter.attribute?.let { attribute -> dc.tags.flatMap { it.attributes }.contains(attribute) } ?: true
                    && filter.text?.let { text -> (if (dc.useOcrTxt) dc.ocrLines else dc.pdfLines).flatMap { it.words }.any { text.contains(it.word.text, true) } } ?: true
        }.toSet()
    }

    companion object {
        private var importing = false
        private val currentImports = mutableSetOf<String>()
        private var _progress = 0.0
    }
}