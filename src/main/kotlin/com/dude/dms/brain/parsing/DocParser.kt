package com.dude.dms.brain.parsing

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.dsl.attributefilter.AttributeFilterParser
import com.dude.dms.brain.dsl.tagFilterLang.TagFilterParser
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.utils.findDate
import com.dude.dms.utils.findDecimal
import com.dude.dms.utils.findInt
import org.springframework.stereotype.Component
import java.io.File
import java.time.LocalDate


@Component
class DocParser(
        private val tagService: TagService,
        private val pdfStripper: DmsPdfTextStripper,
        private val ocrStripper: DmsOcrTextStripper
) {

    fun getPdfText(guid: String) = pdfStripper.getPages(guid, "")

    fun getOcrText(guid: String, language: String = Options.get().doc.ocrLanguage) = ocrStripper.getPages(guid, language)

    fun getText(img: File, rect: DmsOcrTextStripper.Rect, language: String = Options.get().doc.ocrLanguage) = ocrStripper.getTextFromArea(img, rect, language)

    fun discoverTags(doc: DocContainer): Set<TagContainer> {
        val tags = mutableSetOf<TagContainer>()
        tags.addAll(Options.get().tag.automaticTags.mapNotNull { tagService.findByName(it) }.map { TagContainer(it, t("automatic")) })

        val parser = TagFilterParser()
        tagService.findAll().forEach { tag ->
            if (tag.tagFilter != null) {
                val result = parser.setInput(tag.tagFilter!!.filter)
                if (result.tagFilter != null && result.isValid) {
                    if (doc.checkTagFilter(result.tagFilter)) {
                        tags.add(TagContainer(tag))
                    }
                }
            }
        }
        if (tags.isNotEmpty()) LOGGER.info(t("tag.discovered", tags.joinToString(", ") { it.tag.name }))
        return tags
    }

    fun discoverAttributeValues(doc: DocContainer): Set<AttributeValue> {
        val values = mutableSetOf<AttributeValue>()
        val parser = AttributeFilterParser()
        doc.tagEntities.flatMap { it.attributes }.toSet().forEach { attr ->
            if (attr.attributeFilter != null) {
                LOGGER.info(t("attribute.searching", attr.name))
                val result = parser.setInput(attr.attributeFilter!!.filter)
                if (result.attributeFilter != null && result.isValid) {
                    val words = doc.getWordsForAttributeFilter(result.attributeFilter)
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
        }
        return values
    }

    fun getMostFrequentDate(doc: DocContainer): LocalDate? {
        LOGGER.info(t("doc.parse.date.detect"))
        return doc.getFullTextLowerCase().findDate()
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(DocParser::class.java)
    }
}