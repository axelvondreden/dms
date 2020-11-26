package com.dude.dms.brain.parsing

import com.dude.dms.backend.containers.PageContainer
import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.data.docs.Page
import com.dude.dms.backend.data.docs.Word
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.utils.tessdataPath
import org.bytedeco.leptonica.global.lept
import org.bytedeco.tesseract.TessBaseAPI
import org.springframework.stereotype.Component
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

@Component
class DmsOcrTextStripper(private val fileManager: FileManager) : TextStripper {

    val apis by lazy {
        mapOf(
                "eng" to TessBaseAPI().apply { Init(tessdataPath, "eng") },
                "deu" to TessBaseAPI().apply { Init(tessdataPath, "deu") }
        )
    }

    /**
     * Parses all pages of a document for the given [guid] and returns a set of [PageContainer]
     */
    override fun getPages(guid: String, language: String): Set<PageContainer> {
        val api = apis[language]
        val pages = mutableSetOf<PageContainer>()
        fileManager.getImages(guid).forEach {
            LOGGER.info(t("image.ocr.page", language, it.index + 1))
            pages.add(getPage(api!!, it.value, it.index + 1))
        }
        return pages
    }

    /**
     * Parses a single [img] and returns a [PageContainer] for the given [pageNr]
     */
    private fun getPage(api: TessBaseAPI, img: File, pageNr: Int): PageContainer {
        val image = lept.pixRead(img.absolutePath)
        api.SetImage(image)

        val alto = api.GetAltoText(0)
        val xml = alto.string

        alto.deallocate()
        lept.pixDestroy(image)
        api.Clear()

        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml.byteInputStream())
        val page = doc.documentElement
        val textLines = doc.getElementsByTagName("TextLine")

        val pageWidth = page.getAttribute("WIDTH").toFloat()
        val pageHeight = page.getAttribute("HEIGHT").toFloat()

        val lines = mutableSetOf<Line>()

        for (i in 0 until textLines.length) {
            lines.add(getLine(textLines.item(i), pageWidth, pageHeight))
        }
        return PageContainer(Page(null, lines, pageNr))
    }

    /**
     * Parses a single [Line] for a [Page] by combining multiple [Word]s
     */
    private fun getLine(node: Node, pageWidth: Float, pageHeight: Float): Line {
        val words = mutableSetOf<Word>()
        val line = Line(null, words, node.attributes.getNamedItem("VPOS").nodeValue.toFloat() / pageHeight * 100.0F)
        val textWords = node.childNodes
        for (j in 0 until textWords.length) {
            val textWord = textWords.item(j)
            if (textWord.nodeName == "String") {
                val text = textWord.attributes.getNamedItem("CONTENT").nodeValue
                if (!text.isValidWord()) continue

                val width = textWord.attributes.getNamedItem("WIDTH").nodeValue.toFloat() / pageWidth * 100.0F
                val x = textWord.attributes.getNamedItem("HPOS").nodeValue.toFloat() / pageWidth * 100.0F
                val height = textWord.attributes.getNamedItem("HEIGHT").nodeValue.toFloat() / pageHeight * 100.0F
                val y = textWord.attributes.getNamedItem("VPOS").nodeValue.toFloat() / pageHeight * 100.0F
                words.add(Word(line, text, x, y, width, height))
            }
        }
        return line
    }

    /**
     * Searches for text in the specified [rect] of an [img].
     * [language] overwrites the default language
     */
    fun getTextFromArea(img: File, rect: Rect, language: String = Options.get().doc.ocrLanguage): String {
        val api = TessBaseAPI()
        if (api.Init(tessdataPath, language) != 0) {
            LOGGER.error(t("image.ocr.error"))
            return ""
        }

        // Open input image with leptonica library
        val image = lept.pixRead(img.absolutePath)
        api.SetImage(image)
        val xx = image.w().toFloat() * (rect.x / 100.0F)
        val yy = image.h().toFloat() * (rect.y / 100.0F)
        val ww = image.w().toFloat() * (rect.w / 100.0F)
        val hh = image.h().toFloat() * (rect.h / 100.0F)
        api.SetRectangle(xx.toInt(), yy.toInt(), ww.toInt(), hh.toInt())

        val txtPointer = api.GetUTF8Text()
        val txt = txtPointer.string

        api.End()
        txtPointer.deallocate()
        lept.pixDestroy(image)

        return txt
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(DmsOcrTextStripper::class.java)
    }

    data class Rect(val x: Float, val y: Float, val w: Float, val h: Float)
}