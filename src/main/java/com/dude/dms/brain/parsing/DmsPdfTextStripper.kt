package com.dude.dms.brain.parsing

import com.dude.dms.backend.containers.LineContainer
import com.dude.dms.backend.containers.PageContainer
import com.dude.dms.backend.containers.WordContainer
import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.data.docs.Page
import com.dude.dms.backend.data.docs.Word
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition
import org.springframework.stereotype.Component
import kotlin.math.max
import kotlin.math.min

@Component
class DmsPdfTextStripper : PDFTextStripper() {

    private var pages = mutableSetOf<PageContainer>()

    fun getPages(doc: PDDocument): Set<PageContainer> {
        pages = mutableSetOf()
        getText(doc)
        return pages
    }

    override fun writeString(text: String?, textPositions: List<TextPosition>) {
        createLine(textPositions)
        super.writeString(text, textPositions)
    }

    private fun createLine(textPositions: List<TextPosition>) {
        val words = createWords(textPositions.toList(), textPositions[0].pageWidth, textPositions[0].pageHeight)
        if (!words.isNullOrEmpty()) {
            val line = Line(null, words.map { it.word }.toMutableSet(), words.map { it.word.y }.min()!!)
            words.forEach { it.word.line = line }
            val page = pages.firstOrNull { it.nr == currentPageNo }
            if (page != null) page.lines = page.lines.plus(LineContainer(line))
            else pages.add(PageContainer(Page(null, mutableSetOf(line), currentPageNo)))
        }
    }

    private fun createWords(positionsInput: List<TextPosition>, pageWidth: Float, pageHeight: Float): MutableSet<WordContainer> {
        val words = mutableSetOf<WordContainer>()
        var positions = positionsInput
        while (positions.isNotEmpty()) {
            positions = positions.dropWhile { it.unicode == " " }
            val word = positions.takeWhile { it.unicode != " " }
            positions = positions.drop(word.size)
            if (word.isEmpty()) continue

            var xMin = Float.MAX_VALUE
            var xMax = Float.MIN_VALUE
            var yMin = Float.MAX_VALUE
            var yMax = Float.MIN_VALUE
            for (letter in word) {
                xMin = min(xMin, letter.xDirAdj)
                yMin = min(yMin, letter.yDirAdj)
                xMax = max(xMax, letter.xDirAdj + letter.widthDirAdj)
                yMax = max(yMax, letter.yDirAdj + letter.heightDir)
            }
            val width = (xMax - xMin) / pageWidth * 100.0f
            val x = xMin / pageWidth * 100.0f
            val height = (yMax - yMin) / pageHeight * 100.0f * 2.0F
            val y = yMin / pageHeight * 100.0f - height
            words.add(WordContainer(Word(null, word.joinToString("") { it.unicode }, x, y, width, height)))
        }
        return words
    }
}