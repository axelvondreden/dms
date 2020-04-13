package com.dude.dms.brain.polling

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.LineContainer
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.brain.parsing.Spellchecker
import com.dude.dms.brain.t
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

    fun import() {
        val pdfs = File(Options.get().doc.pollingPath).listFiles { _, name -> name.endsWith(".pdf") } ?: emptyArray()
        val imgs = File(Options.get().doc.pollingPath).listFiles { _, name -> Const.IMAGE_FORMATS.any { name.endsWith(it) } } ?: emptyArray()
        val dcs = mutableSetOf<DocContainer>()
        for (pdf in pdfs) {
            dcs.add(fileManager.importPdf(pdf, true)?.let { guid -> DocContainer(guid) } ?: continue)
        }
        for (img in imgs) {
            dcs.add(fileManager.importImage(img, true)?.let { guid -> DocContainer(guid) } ?: continue)
        }
        val spellcheckers = Const.OCR_LANGUAGES.map { it to Spellchecker(it) }
        dcs.forEach { dc ->
            dc.pdfText = docParser.getPdfText(dc.guid).map { LineContainer(it) }.toSet()
            if (dc.pdfText.isNotEmpty()) {
                dc.language = spellcheckers.minBy { pair -> dc.pdfText.flatMap { it.words }.sumBy { pair.second.check(it.word.text).size } }!!.first
            }
            dc.ocrText = docParser.getOcrText(fileManager.getFirstImage(dc.guid), dc.language).map { LineContainer(it) }.toSet()
            dc.pdfSpelling = docParser.checkSpelling(dc.pdfText!!, dc.language)
            dc.ocrSpelling = docParser.checkSpelling(dc.ocrText!!, dc.language)
            if (!processing) return
            ui.access {
                progressBar.value = (index + 1).toDouble()
                cardWrapper.add(builderFactory.docs().textImportCard(dc))
            }
        }
    }

    fun findByFilter(filter: Filter): Set<DocContainer> {
        return docs.filter { dc ->
            filter.tag?.let { dc.tags.contains(it) } ?: true
                    && filter.attribute?.let { attribute -> dc.tags.flatMap { it.attributes }.contains(attribute) } ?: true
                    && filter.text?.let { text -> (if (dc.useOcrTxt) dc.ocrText else dc.pdfText).flatMap { it.words }.any { text.contains(it.word.text, true) } } ?: true
        }.toSet()
    }
}