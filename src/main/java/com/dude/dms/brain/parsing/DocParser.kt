package com.dude.dms.brain.parsing

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.data.docs.Word
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.LineService
import com.dude.dms.backend.service.TagService
import com.dude.dms.backend.service.WordService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.options.Options
import org.apache.pdfbox.pdmodel.PDDocument
import org.bytedeco.leptonica.global.lept.pixDestroy
import org.bytedeco.leptonica.global.lept.pixRead
import org.bytedeco.tesseract.TessBaseAPI
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory


@Component
class DocParser(
        private val docService: DocService,
        private val tagService: TagService,
        private val lineService: LineService,
        private val wordService: WordService,
        private val plainTextRuleValidator: PlainTextRuleValidator,
        private val regexRuleValidator: RegexRuleValidator,
        private val fileManager: FileManager,
        private val pdfStripper: DmsPdfTextStripper
) {

    /**
     * Creates a new [Doc] from imported files
     *
     * @param guid GUID of the imported files
     */
    fun parse(guid: String) {
        var lines = setOf<Line>()

        try {
            PDDocument.load(fileManager.getPdf(guid)).use { pdDoc ->
                lines = stripText(pdDoc)
            }
            if (lines.isEmpty()) {
                lines = ocrText(fileManager.getFirstImage(guid))
            }
            val doc = Doc(guid)
            if (lines.isNotEmpty()) {
                doc.rawText = docService.getFullTextMemory(lines)
                doc.documentDate = discoverDates(doc.rawText!!)
            }
            doc.tags = discoverTags(doc.rawText).toMutableSet()
            docService.create(doc)
            lines.forEach { line ->
                line.doc = doc
                lineService.create(line)
                line.words.forEach {
                    wordService.create(it)
                }
            }
        } catch (e: IOException) {
            e.message?.let { LOGGER.error(it) }
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
        for (tag in Options.get().tag.automaticTags) {
            tagService.findByName(tag)?.let {
                LOGGER.info("Adding tag: {}", it.name)
                tags.add(it)
            }
        }
        if (!rawText.isNullOrEmpty()) {
            tags.addAll(plainTextRuleValidator.getTags(rawText))
            tags.addAll(regexRuleValidator.getTags(rawText))
        }
        return tags
    }

    private fun stripText(pdDoc: PDDocument): Set<Line> {
        LOGGER.info("Stripping text...")
        return pdfStripper.getLines(pdDoc)
    }

    private fun ocrText(img: File): Set<Line> {
        val ocrLang = Options.get().doc.ocrLanguage
        LOGGER.info("Running OCR ($ocrLang)...")
        val api = TessBaseAPI()
        if (api.Init("tessdata", ocrLang) != 0) {
            LOGGER.error("Could not initialize tesseract.")
            return emptySet()
        }

        // Open input image with leptonica library
        val image = pixRead(img.absolutePath)
        api.SetImage(image)

        val alto = api.GetAltoText(0)
        val xml = alto.string

        api.End()
        alto.deallocate()
        pixDestroy(image)

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()

        // Parse Alto-XML
        val doc = builder.parse(xml.byteInputStream())
        val page = doc.documentElement
        val textLines = doc.getElementsByTagName("TextLine")


        val pageWidth = page.getAttribute("WIDTH").toFloat()
        val pageHeight = page.getAttribute("HEIGHT").toFloat()

        val lines = mutableSetOf<Line>()

        for (i in 0 until textLines.length) {
            val textLine = textLines.item(i)
            val words = mutableSetOf<Word>()
            val line = Line(null, words, textLine.attributes.getNamedItem("VPOS").nodeValue.toFloat() / pageHeight * 100.0F)
            val textWords = textLine.childNodes
            for (j in 0 until textWords.length) {
                val textWord = textWords.item(j)
                if (textWord.nodeName == "String") {
                    val width = textWord.attributes.getNamedItem("WIDTH").nodeValue.toFloat() / pageWidth * 100.0F
                    val x = textWord.attributes.getNamedItem("HPOS").nodeValue.toFloat() / pageWidth * 100.0F
                    val height = textWord.attributes.getNamedItem("HEIGHT").nodeValue.toFloat() / pageHeight * 100.0F
                    val y = textWord.attributes.getNamedItem("VPOS").nodeValue.toFloat() / pageHeight * 100.0F
                    val text = textWord.attributes.getNamedItem("CONTENT").nodeValue
                    words.add(Word(line, text, x, y, width, height))
                }
            }
            lines.add(line)
        }
        return lines
    }

    private fun discoverDates(rawText: String): LocalDate? {
        LOGGER.info("Trying to find date...")
        val map = mutableMapOf<LocalDate, Int>()
        for (pattern in Options.get().view.dateScanFormats) {
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
        map.entries.maxBy { it.value }?.let {
            LOGGER.info("Setting date as {} with {} occurences", it.key, it.value)
            return it.key
        }
        LOGGER.info("No date found on document")
        return null
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(DocParser::class.java)
    }
}