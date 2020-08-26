package com.dude.dms.brain.parsing

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.extensions.findDate
import com.dude.dms.extensions.findDecimal
import com.dude.dms.extensions.findInt
import org.springframework.stereotype.Component
import java.io.File
import java.time.LocalDate


@Component
class DocParser(
        private val tagService: TagService,
        private val plainTextRuleValidator: PlainTextRuleValidator,
        private val regexRuleValidator: RegexRuleValidator,
        private val pdfStripper: DmsPdfTextStripper,
        private val ocrStripper: DmsOcrTextStripper
) {

    fun getPdfText(guid: String) = pdfStripper.getPages(guid, "")

    fun getOcrText(guid: String, language: String = Options.get().doc.ocrLanguage) = ocrStripper.getPages(guid, language)

    fun getText(img: File, rect: DmsOcrTextStripper.Rect, language: String = Options.get().doc.ocrLanguage) = ocrStripper.getText(img, rect, language)

    fun discoverTags(doc: DocContainer): Set<TagContainer> {
        val docText = doc.getFullText()
        val tags = mutableSetOf<TagContainer>()
        tags.addAll(Options.get().tag.automaticTags.mapNotNull { tagService.findByName(it) }.map { TagContainer(it, t("automatic")) })
        if (docText.isNotEmpty()) {
            tags.addAll(plainTextRuleValidator.getTags(docText))
            tags.addAll(regexRuleValidator.getTags(docText))
        }
        LOGGER.info(t("tag.discovered", tags.joinToString(", ") { it.tag.name }))
        return tags
    }

    fun discoverAttributeValues(doc: DocContainer): Set<AttributeValue> {
        val values = mutableSetOf<AttributeValue>()
        doc.tagEntities.flatMap { it.attributes }.toSet().forEach { attr ->
            if (attr.condition != null) {
                LOGGER.info(t("attribute.searching", attr.name))
                val words = doc.words.filter { attr.condition!!.validate(doc, it) }
                LOGGER.info("Found ${words.size} possible values")
                val filtered = words.filter {
                    when (attr.type) {
                        Attribute.Type.INT -> it.text.findInt() != null
                        Attribute.Type.FLOAT -> it.text.findDecimal() != null
                        Attribute.Type.DATE -> it.text.findDate() != null
                        else -> true
                    }
                }
                val value = filtered.groupBy { it.text }.maxByOrNull { it.value.size }?.key
                if (value != null) {
                    LOGGER.info("Found value: $value")
                    val av = AttributeValue(doc.doc, attr)
                    when (attr.type) {
                        Attribute.Type.STRING -> av.stringValue = value
                        Attribute.Type.INT -> av.intValue = value.findInt()
                        Attribute.Type.FLOAT -> av.floatValue = value.findDecimal()!!.toFloat()
                        Attribute.Type.DATE -> av.dateValue = value.findDate()
                    }
                    values.add(av)
                }
            }
        }
        return values
    }

    fun getMostFrequentDate(doc: DocContainer): LocalDate? {
        LOGGER.info(t("doc.parse.date.detect"))
        return doc.getFullText().findDate()
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(DocParser::class.java)
    }
}