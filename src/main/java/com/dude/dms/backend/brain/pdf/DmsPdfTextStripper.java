package com.dude.dms.backend.brain.pdf;

import com.dude.dms.backend.data.base.Word;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.List;

public class DmsPdfTextStripper extends PDFTextStripper {

    private List<Word> wordListOut;

    public DmsPdfTextStripper() throws IOException {
    }

    public String getTextWithPositions(PDDocument doc, List<Word> wordListOut) throws IOException {
        this.wordListOut = wordListOut;
        return getText(doc);
    }

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
        createWordEntity(text, textPositions);
        super.writeString(text, textPositions);
    }

    private void createWordEntity(String text, Iterable<TextPosition> textPositions) {
        float xMin = Float.MAX_VALUE;
        float yMin = Float.MAX_VALUE;
        float xMax = Float.MIN_VALUE;
        float yMax = Float.MIN_VALUE;
        for (TextPosition textPosition : textPositions) {
            xMin = Math.min(xMin, textPosition.getX());
            yMin = Math.min(yMin, textPosition.getY());
            xMax = Math.max(xMax, textPosition.getEndX());
            yMax = Math.max(yMax, textPosition.getEndY());
        }
        wordListOut.add(new Word(text, xMin, yMin, xMax - xMin, yMax - yMin));
    }
}