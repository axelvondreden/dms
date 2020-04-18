package com.dude.dms.brain.parsing

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.data.docs.Word
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import org.apache.pdfbox.pdmodel.PDDocument
import org.bytedeco.leptonica.global.lept.pixDestroy
import org.bytedeco.leptonica.global.lept.pixRead
import org.bytedeco.tesseract.TessBaseAPI
import org.springframework.stereotype.Component
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.xml.parsers.DocumentBuilderFactory


@Component
class DocParser(
        private val docService: DocService,
        private val tagService: TagService,
        private val plainTextRuleValidator: PlainTextRuleValidator,
        private val regexRuleValidator: RegexRuleValidator,
        private val fileManager: FileManager,
        private val pdfStripper: DmsPdfTextStripper
) {

    fun getPdfText(guid: String): Set<Line> {
        LOGGER.info(t("pdf.parse"))
        PDDocument.load(fileManager.getPdf(guid)).use { return pdfStripper.getLines(it) }
    }

    fun getOcrText(img: File, ocrLang: String = Options.get().doc.ocrLanguage): Set<Line> {
        LOGGER.info(t("image.ocr", ocrLang))
        val api = TessBaseAPI()
        if (api.Init("tessdata", ocrLang) != 0) {
            LOGGER.error(t("image.ocr.error"))
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

    fun getOcrTextRect(img: File, x: Float, y: Float, w: Float, h: Float, ocrLang: String = Options.get().doc.ocrLanguage): String {
        val api = TessBaseAPI()
        if (api.Init("tessdata", ocrLang) != 0) {
            LOGGER.error(t("image.ocr.error"))
            return ""
        }

        // Open input image with leptonica library
        val image = pixRead(img.absolutePath)
        api.SetImage(image)
        val xx = image.w().toFloat() * (x / 100.0F)
        val yy = image.h().toFloat() * (y / 100.0F)
        val ww = image.w().toFloat() * (w / 100.0F)
        val hh = image.h().toFloat() * (h / 100.0F)
        api.SetRectangle(xx.toInt(), yy.toInt(), ww.toInt(), hh.toInt())

        val txtPointer = api.GetUTF8Text()
        val txt = txtPointer.string

        api.End()
        txtPointer.deallocate()
        pixDestroy(image)

        return txt
    }

    fun discoverTags(lines: Set<Line>): Set<Tag> {
        val rawText = docService.getFullTextMemory(lines)
        val tags = Options.get().tag.automaticTags.mapNotNull { tagService.findByName(it) }.toMutableSet()
        if (rawText.isNotEmpty()) {
            tags.addAll(plainTextRuleValidator.getTags(rawText))
            tags.addAll(regexRuleValidator.getTags(rawText))
        }
        LOGGER.info(t("tag.discovered", tags.joinToString(", ") { it.name }))
        return tags
    }

    fun getMostFrequentDate(lines: Set<Line>): LocalDate? {
        LOGGER.info(t("doc.parse.date.detect"))
        val rawText = docService.getFullTextMemory(lines)
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
            LOGGER.info(t("doc.parse.date.detect.success", it.key, it.value))
            return it.key
        }
        LOGGER.info(t("doc.parse.date.detect.failed"))
        return null
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(DocParser::class.java)
    }
}