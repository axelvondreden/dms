package com.dude.dms.brain.parsing

import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.data.docs.Word
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition
import org.springframework.stereotype.Component
import kotlin.math.max
import kotlin.math.min

@Component
class DmsPdfTextStripper : PDFTextStripper() {

    private var lines = mutableSetOf<Line>()

    fun getLines(doc: PDDocument): Set<Line> {
        lines = mutableSetOf()
        getText(doc)
        return lines
    }

    override fun writeString(text: String?, textPositions: List<TextPosition>) {
        createLine(textPositions)
        super.writeString(text, textPositions)
    }

    private fun createLine(textPositions: List<TextPosition>) {
        val pageWidth = textPositions[0].pageWidth
        val pageHeight = textPositions[0].pageHeight

        var positions = textPositions.toList()
        val words = mutableSetOf<Word>()
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
            words.add(Word(null, word.joinToString("") { it.unicode }, x, y, width, height))
        }
        if (!words.isNullOrEmpty()) {
            val line = Line(null, words, words.map { it.y }.min()!!)
            words.forEach { it.line = line }
            lines.add(line)
        }
    }
}