package com.dude.dms.brain.polling

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.PageContainer
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
import kotlinx.coroutines.*
import org.springframework.stereotype.Component
import java.io.File
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

    private val docs = mutableSetOf<DocContainer>()

    val progress
        get() = progressBacking

    val progressText
        get() = progressTextBacking

    fun calculateCount(): Int {
        val count = pdfs.size + imgs.size
        if (count > docs.size && !importing) import()
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
        progressBacking = 0.0
        GlobalScope.launch {
            try {
                val dcs = mutableSetOf<DocContainer>()
                val newPdfs = pdfs.filter { pdf -> pdf.name !in docs.mapNotNull { it.file?.name } && pdf.name !in currentImports }
                for (pdf in newPdfs.withIndex()) {
                    progressTextBacking = "${pdf.index + 1} / ${newPdfs.size} ${t("saving", pdf.value.name)}"
                    progressBacking = pdf.index.toDouble() / newPdfs.size
                    dcs.add(fileManager.importPdf(pdf.value, false)?.let { guid -> DocContainer(guid, pdf.value) }
                            ?: continue)
                }
                val newImgs = imgs.filter { img -> img.name !in docs.mapNotNull { it.file?.name } && img.name !in currentImports }
                for (img in newImgs.withIndex()) {
                    progressTextBacking = "${img.index + 1} / ${newImgs.size} ${t("saving", img.value.name)}"
                    progressBacking = img.index.toDouble() / newImgs.size
                    dcs.add(fileManager.importImage(img.value, false)?.let { guid -> DocContainer(guid, img.value) }
                            ?: continue)
                }
                currentImports.addAll(dcs.mapNotNull { it.file?.name })
                val spellcheckers = Const.OCR_LANGUAGES.associate { it to Spellchecker(it) }
                val size = dcs.size
                val progressMax = size * 4.0
                progressBacking = 0.0
                dcs.forEachIndexed { index, dc ->
                    progressTextBacking = "${index + 1} / $size    " + t("pdf.parse")

                    val pdfPages = async { docParser.getPdfText(dc.guid) }
                    val ocrPages = async { docParser.getOcrText(dc.guid, dc.language) }

                    dc.pdfPages = pdfPages.await()
                    if (dc.pdfPages.isNotEmpty()) {
                        dc.language = spellcheckers.minByOrNull { entry -> dc.pdfPages.flatMap { it.lines }.flatMap { it.words }.count { container -> container.word.text?.let { entry.value.check(it) } != null } }!!.key
                    }
                    progressTextBacking += " > ${t("recognized.language")}: ${dc.language}"
                    progressBacking = (index * 4 + 1) / progressMax
                    dc.pdfPages.forEach { it.image = fileManager.getImage(dc.guid, it.nr) }
                    progressTextBacking += " > " + t("image.ocr", dc.language)

                    dc.ocrPages = ocrPages.await()
                    dc.ocrPages.forEach { it.image = fileManager.getImage(dc.guid, it.nr) }
                    progressBacking = (index * 4 + 2) / progressMax
                    dc.pdfPages.words().forEach { container -> container.spelling = container.word.text?.let { spellcheckers.getValue(dc.language).check(it) } }
                    dc.ocrPages.words().forEach { container -> container.spelling = container.word.text?.let { spellcheckers.getValue(dc.language).check(it) } }
                    progressBacking = (index * 4 + 3) / progressMax

                    dc.useOcrTxt = dc.ocrPages.wordCount() > dc.pdfPages.wordCount()

                    dc.date = docParser.getMostFrequentDate(dc)
                    dc.tags = docParser.discoverTags(dc).toMutableSet()
                    dc.attributeValues = docParser.discoverAttributeValues(dc).map { it.key }.toMutableSet()

                    docs.add(dc)
                    currentImports.remove(dc.file?.name)
                    progressBacking = (index * 4 + 4) / progressMax
                }
            } finally {
                progressBacking = 1.0
                importing = false
            }
        }
    }

    fun create(docContainer: DocContainer) {
        val pages = docContainer.pages
        val doc = Doc(docContainer.guid, null, docContainer.date, LocalDateTime.now(), docContainer.tagEntities.toMutableSet())
        docService.create(doc, docContainer.attributeValues)
        pages.forEach { pageContainer ->
            val page = pageContainer.page
            page.doc = doc
            pageService.create(page)
            pageContainer.lines.forEach { lineContainer ->
                val line = lineContainer.line
                line.page = page
                lineService.create(line)
                lineContainer.words.forEach { wordContainer ->
                    val word = wordContainer.word
                    word.line = line
                    wordService.create(word)
                }
            }
        }
        docService.save(doc)
        delete(docContainer)
    }

    fun delete(docContainer: DocContainer) {
        docContainer.file?.delete()
        docs.remove(docContainer)
    }

    fun findAll() = docs.toSet()

    private fun Set<PageContainer>.words() = flatMap { it.lines }.flatMap { it.words }

    private fun Set<PageContainer>.wordCount() = flatMap { it.lines }.sumOf { it.words.size }

    companion object {
        private var importing = false
        private val currentImports = mutableSetOf<String>()
        private var progressBacking = 0.0
        private var progressTextBacking = ""
    }
}