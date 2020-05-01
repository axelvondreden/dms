package com.dude.dms.brain.parsing

import com.dude.dms.backend.containers.PageContainer
import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.data.docs.Page
import com.dude.dms.backend.data.docs.Word
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import org.bytedeco.leptonica.global.lept
import org.bytedeco.tesseract.TessBaseAPI
import org.springframework.stereotype.Component
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

@Component
class DmsOcrTextStripper(private val fileManager: FileManager) : TextStripper {

    override fun getPages(guid: String, language: String): Set<PageContainer> {
        val api = TessBaseAPI()
        if (api.Init("tessdata", language) != 0) {
            LOGGER.error(t("image.ocr.error"))
            return emptySet()
        }

        val pages = mutableSetOf<PageContainer>()
        fileManager.getImages(guid).forEach {
            LOGGER.info(t("image.ocr.page", language, it.index + 1))

            // Open input image with leptonica library
            val image = lept.pixRead(it.value.absolutePath)
            api.SetImage(image)

            val alto = api.GetAltoText(0)
            val xml = alto.string

            alto.deallocate()
            lept.pixDestroy(image)

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
                        val text = textWord.attributes.getNamedItem("CONTENT").nodeValue
                        if (!text.isValidWord()) continue

                        val width = textWord.attributes.getNamedItem("WIDTH").nodeValue.toFloat() / pageWidth * 100.0F
                        val x = textWord.attributes.getNamedItem("HPOS").nodeValue.toFloat() / pageWidth * 100.0F
                        val height = textWord.attributes.getNamedItem("HEIGHT").nodeValue.toFloat() / pageHeight * 100.0F
                        val y = textWord.attributes.getNamedItem("VPOS").nodeValue.toFloat() / pageHeight * 100.0F
                        words.add(Word(line, text, x, y, width, height))
                    }
                }
                lines.add(line)
            }
            pages.add(PageContainer(Page(null, lines, it.index + 1)))
        }
        api.End()
        return pages
    }

    fun getText(img: File, rect: Rect, language: String = Options.get().doc.ocrLanguage): String {
        val api = TessBaseAPI()
        if (api.Init("tessdata", language) != 0) {
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