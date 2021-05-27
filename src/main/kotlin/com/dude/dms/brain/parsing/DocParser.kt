package com.dude.dms.brain.parsing

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.filter.AttributeFilter
import com.dude.dms.backend.data.filter.TagFilter
import com.dude.dms.backend.service.TagFilterService
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
    private val tagFilterService: TagFilterService,
    private val pdfStripper: DmsPdfTextStripper,
    private val ocrStripper: DmsOcrTextStripper
) {

    fun getPdfText(guid: String) = pdfStripper.getPages(guid, "")

    fun getOcrText(guid: String, language: String = Options.get().doc.ocrLanguage) = ocrStripper.getPages(guid, language)

    fun getText(img: File, rect: DmsOcrTextStripper.Rect, language: String = Options.get().doc.ocrLanguage) = ocrStripper.getTextFromArea(img, rect, language)

    fun discoverTags(doc: DocContainer) = filterTags(doc, tagFilterService.findAll()).map { it.key }
        .plus(Options.get().tag.automaticTags
            .mapNotNull { tagService.findByName(it) }
            .map { TagContainer(it, t("automatic")) }).toSet()

    fun filterTags(doc: DocContainer, filters: Collection<TagFilter>): Map<TagContainer, Int> {
        val parser = TagFilterParser()
        return filters
            .associateWith { parser.setInput(it.filter) }
            .filter { it.value.tagFilter != null && it.value.isValid }
            .mapNotNull { pair -> doc.checkTagFilter(pair.value.tagFilter!!)?.let { TagContainer(pair.key.tag) to it.y.toInt() } }
            .toMap()
    }

    fun discoverAttributeValues(doc: DocContainer, filters: Collection<AttributeFilter> = doc.tagEntities.flatMap { it.attributes }.mapNotNull { it.attributeFilter }.toSet()): Map<AttributeValue, Int> {
        val values = mutableMapOf<AttributeValue, Int>()
        val parser = AttributeFilterParser()
        filters.forEach { filter ->
            LOGGER.info(t("attribute.searching", filter.attribute.name))
            val result = parser.setInput(filter.filter)
            if (result.attributeFilter != null && result.isValid) {
                val words = doc.getWordsForAttributeFilter(result.attributeFilter)
                LOGGER.info("Found ${words.size} possible values")
                val filtered = words.filter {
                    when (filter.attribute.type) {
                        Attribute.Type.INT -> it.text.findInt() != null
                        Attribute.Type.FLOAT -> it.text.findDecimal() != null
                        Attribute.Type.DATE -> it.text.findDate() != null
                        else -> true
                    }
                }
                val finalWord = filtered.groupBy { it.text }.maxByOrNull { it.value.size }
                val value = finalWord?.key
                if (value != null) {
                    LOGGER.info("Found value: $value")
                    val av = AttributeValue(doc.doc, filter.attribute)
                    when (filter.attribute.type) {
                        Attribute.Type.STRING -> av.stringValue = value
                        Attribute.Type.INT -> av.intValue = value.findInt()
                        Attribute.Type.FLOAT -> av.floatValue = value.findDecimal()!!.toFloat()
                        Attribute.Type.DATE -> av.dateValue = value.findDate()
                    }
                    values[av] = finalWord.value.first().word.y.toInt()
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
