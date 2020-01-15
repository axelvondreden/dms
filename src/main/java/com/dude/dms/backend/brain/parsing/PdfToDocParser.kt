package com.dude.dms.backend.brain.parsing

import com.dude.dms.backend.brain.DmsLogger
import com.dude.dms.backend.brain.FileManager
import com.dude.dms.backend.brain.OptionKey
import com.dude.dms.backend.brain.ParseEvent
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.TextBlock
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.backend.service.TextBlockService
import org.apache.pdfbox.pdmodel.PDDocument
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

@Component
class PdfToDocParser(
        private val docService: DocService,
        private val tagService: TagService,
        private val textBlockService: TextBlockService,
        private val plainTextRuleValidator: PlainTextRuleValidator,
        private val regexRuleValidator: RegexRuleValidator
) : Parser {

    private var textBlockList: List<TextBlock>? = null

    private val eventListeners = HashMap<String, ParseEvent>()

    /**
     * Creates a new [Doc] from a file.
     *
     * @param file the file used for parsing, this has a to be a pdf file.
     */
    override fun parse(file: File?) {
        LOGGER.info("Parsing file {}...", file!!.name)
        val guid = UUID.randomUUID().toString()
        var rawText: String? = null
        val savedFile = FileManager.savePdf(file, guid)
        if (savedFile.isPresent) {
            try {
                PDDocument.load(savedFile.get()).use { pdDoc ->
                    FileManager.saveImage(pdDoc, guid)
                    rawText = stripText(pdDoc)
                }
            } catch (e: IOException) {
                e.message?.let { LOGGER.error(it) }
            }
            val doc = Doc(guid)
            if (rawText != null && rawText!!.isNotEmpty()) {
                doc.rawText = rawText
                doc.documentDate = discoverDates(rawText!!)
            }
            doc.tags = discoverTags(rawText).toMutableSet()
            docService.create(doc)
            LOGGER.info("Created doc with ID: {}", doc.guid)
            if (textBlockList != null) {
                textBlockList!!.forEach {
                    it.doc = doc
                    textBlockService.create(it)
                    LOGGER.info("Created textblock {} for doc {}", it, doc)
                }
            }
            eventListeners.values.forEach { it.invoke(true) }
        } else {
            LOGGER.error("Could not save file {}", file.absolutePath)
            eventListeners.values.forEach { it.invoke(false) }
        }
    }

    /**
     * Tries to find tags for a doc by all available methods
     *
     * @param rawText raw text of the document
     * @return list of tags
     */
    private fun discoverTags(rawText: String?): Set<Tag> {
        val tags: MutableSet<Tag> = HashSet()
        if (OptionKey.AUTO_TAG.boolean) {
            tagService.load(OptionKey.AUTO_TAG_ID.long)?.let {
                LOGGER.info("Adding tag: {}", it.name)
                tags.add(it)
            }
        }
        if (rawText != null && rawText.isNotEmpty()) {
            tags.addAll(plainTextRuleValidator.getTags(rawText))
            tags.addAll(regexRuleValidator.getTags(rawText))
        }
        return tags
    }

    private fun stripText(pdDoc: PDDocument): String {
        val pdfStripper = DmsPdfTextStripper()
        LOGGER.info("Stripping text...")
        textBlockList = ArrayList()
        return pdfStripper.getTextWithPositions(pdDoc, textBlockList as ArrayList<TextBlock>)
    }

    fun addEventListener(key: String, parseEvent: ParseEvent) {
        eventListeners[key] = parseEvent
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(PdfToDocParser::class.java)

        private fun discoverDates(rawText: String): LocalDate? {
            LOGGER.info("Trying to find date...")
            val datePatterns = OptionKey.DATE_SCAN_FORMATS.string.split(",").toTypedArray()
            val map = HashMap<LocalDate, Int>()
            for (pattern in datePatterns) {
                for (line in rawText.split("\n").toTypedArray()) {
                    for (i in 0 until line.length - pattern.length) {
                        val snippet = line.substring(i, i + pattern.length)
                        try {
                            val date = LocalDate.parse(snippet, DateTimeFormatter.ofPattern(pattern))
                            map[date] = map.getOrDefault(date, 0) + 1
                        } catch (ignored: DateTimeParseException) {
                        }
                    }
                }
            }
            val entry: Map.Entry<LocalDate, Int>? = map.entries.maxBy { it.value }
            if (entry != null) {
                LOGGER.info("Setting date as {} with {} occurences", entry.key, entry.value)
                return entry.key
            }
            LOGGER.info("No date found on document")
            return null
        }
    }
}