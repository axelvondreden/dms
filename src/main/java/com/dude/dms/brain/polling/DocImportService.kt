package com.dude.dms.brain.polling

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.PageContainer
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.LineService
import com.dude.dms.backend.service.PageService
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
        private val pageService: PageService,
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
        get() = File(Options.get().doc.pollingPath).listFiles { _, name -> Const.IMAGE_FORMATS.any { name.endsWith(it) } }
                ?: emptyArray()

    fun import() {
        if (importing) return
        importing = true
        _progress = 0.0
        try {
            val dcs = mutableSetOf<DocContainer>()
            val newPdfs = pdfs.filter { pdf -> pdf.name !in docs.mapNotNull { it.file?.name } && pdf.name !in currentImports }
            for (pdf in newPdfs.withIndex()) {
                _progressText = "${pdf.index + 1} / ${newPdfs.size} ${t("saving", pdf.value.name)}"
                _progress = pdf.index.toDouble() / newPdfs.size
                dcs.add(fileManager.importPdf(pdf.value, false)?.let { guid -> DocContainer(guid, pdf.value) } ?: continue)
            }
            val newImgs = imgs.filter { img -> img.name !in docs.mapNotNull { it.file?.name } && img.name !in currentImports }
            for (img in newImgs.withIndex()) {
                _progressText = "${img.index + 1} / ${newImgs.size} ${t("saving", img.value.name)}"
                _progress = img.index.toDouble() / newImgs.size
                dcs.add(fileManager.importImage(img.value, false)?.let { guid -> DocContainer(guid, img.value) } ?: continue)
            }
            currentImports.addAll(dcs.mapNotNull { it.file?.name })
            val spellcheckers = Const.OCR_LANGUAGES.map { it to Spellchecker(it) }.toMap()
            val size = dcs.size
            val progressMax = size * 4.0
            _progress = 0.0
            dcs.forEachIndexed { index, dc ->
                _progressText = "${index + 1} / $size    " + t("pdf.parse")
                dc.pdfPages = docParser.getPdfText(dc.guid)
                if (dc.pdfPages.isNotEmpty()) {
                    dc.language = spellcheckers.minBy { entry -> dc.pdfPages.flatMap { it.lines }.flatMap { it.words }.count { entry.value.check(it.word.text) != null } }!!.key
                }
                _progressText += " > ${t("recognized.language")}: ${dc.language}"
                _progress = (index * 4 + 1) / progressMax
                dc.pdfPages.forEach { it.image = fileManager.getImage(dc.guid, it.nr) }
                _progressText += " > " + t("image.ocr", dc.language)

                dc.ocrPages = docParser.getOcrText(dc.guid, dc.language)
                dc.ocrPages.forEach { it.image = fileManager.getImage(dc.guid, it.nr) }
                _progress = (index * 4 + 2) / progressMax
                dc.pdfPages.words().forEach { it.spelling = spellcheckers.getValue(dc.language).check(it.word.text) }
                dc.ocrPages.words().forEach { it.spelling = spellcheckers.getValue(dc.language).check(it.word.text) }
                _progress = (index * 4 + 3) / progressMax

                dc.useOcrTxt = dc.ocrPages.wordCount() > dc.pdfPages.wordCount()

                dc.date = docParser.getMostFrequentDate(dc.pageEntities)
                dc.tags = docParser.discoverTags(dc.pages).toMutableSet()

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
        val pages = docContainer.pageEntities
        val doc = Doc(docContainer.guid, docContainer.date, LocalDateTime.now(), docContainer.tagEntities.toMutableSet())
        docService.create(doc, docContainer.attributeValues)
        pages.forEach { page ->
            page.doc = doc
            pageService.create(page)
            page.lines.forEach { line ->
                line.page = page
                lineService.create(line)
                line.words.forEach { word ->
                    word.line = line
                    wordService.create(word)
                }
            }
        }
        delete(docContainer)
    }

    fun delete(docContainer: DocContainer) {
        docContainer.file?.delete()
        docs.remove(docContainer)
    }

    fun findAll() = docs.toSet()

    private fun Set<PageContainer>.words() = flatMap { it.lines }.flatMap { it.words }

    private fun Set<PageContainer>.wordCount() = flatMap { it.lines }.sumBy { it.words.size }

    companion object {
        private var importing = false
        private val currentImports = mutableSetOf<String>()
        private var _progress = 0.0
        private var _progressText = ""
    }
}