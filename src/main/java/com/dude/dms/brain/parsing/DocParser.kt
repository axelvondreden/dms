package com.dude.dms.brain.parsing

import com.dude.dms.backend.containers.PageContainer
import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.docs.Page
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.extensions.findDate
import org.springframework.stereotype.Component
import java.io.File
import java.time.LocalDate


@Component
class DocParser(
        private val docService: DocService,
        private val tagService: TagService,
        private val plainTextRuleValidator: PlainTextRuleValidator,
        private val regexRuleValidator: RegexRuleValidator,
        private val pdfStripper: DmsPdfTextStripper,
        private val ocrStripper: DmsOcrTextStripper
) {

    fun getPdfText(guid: String) = pdfStripper.getPages(guid, "")

    fun getOcrText(guid: String, language: String = Options.get().doc.ocrLanguage) = ocrStripper.getPages(guid, language)

    fun getText(img: File, rect: DmsOcrTextStripper.Rect, language: String = Options.get().doc.ocrLanguage) = ocrStripper.getText(img, rect, language)

    fun discoverTags(pages: Set<PageContainer>): Set<TagContainer> {
        val docText = docService.getFullText2(pages)
        val tags = mutableSetOf<TagContainer>()
        tags.addAll(Options.get().tag.automaticTags.mapNotNull { tagService.findByName(it) }.map { TagContainer(it, t("automatic")) })
        if (docText.isNotEmpty()) {
            tags.addAll(plainTextRuleValidator.getTags(docText))
            tags.addAll(regexRuleValidator.getTags(docText))
        }
        LOGGER.info(t("tag.discovered", tags.joinToString(", ") { it.tag.name }))
        return tags
    }

    fun getMostFrequentDate(pages: Set<Page>): LocalDate? {
        LOGGER.info(t("doc.parse.date.detect"))
        return docService.getFullText(pages).findDate()
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(DocParser::class.java)
    }
}