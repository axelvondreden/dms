package com.dude.dms.backend.brain.pdf;

import com.dude.dms.backend.data.docs.TextBlock;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.List;

public class DmsPdfTextStripper extends PDFTextStripper {

    private List<TextBlock> textBlockListOut;

    public DmsPdfTextStripper() throws IOException {
    }

    public String getTextWithPositions(PDDocument doc, List<TextBlock> textBlockListOut) throws IOException {
        this.textBlockListOut = textBlockListOut;
        return getText(doc);
    }

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
        createTextBlockEntity(text, textPositions);
        super.writeString(text, textPositions);
    }

    private void createTextBlockEntity(String text, List<TextPosition> textPositions) {
        if (text != null && !text.isEmpty() && textPositions != null && !textPositions.isEmpty()) {
            float xMin = Float.MAX_VALUE;
            float yMin = Float.MAX_VALUE;
            float xMax = Float.MIN_VALUE;
            float yMax = Float.MIN_VALUE;
            float pageWidth = textPositions.get(0).getPageWidth();
            float pageHeight = textPositions.get(0).getPageHeight();
            float fontSize = textPositions.get(0).getFontSizeInPt();
            for (TextPosition textPosition : textPositions) {
                xMin = Math.min(xMin, textPosition.getXDirAdj());
                yMin = Math.min(yMin, textPosition.getYDirAdj());
                xMax = Math.max(xMax, textPosition.getXDirAdj() + textPosition.getWidthDirAdj());
                yMax = Math.max(yMax, textPosition.getYDirAdj() + textPosition.getHeightDir());
            }
            float width = ((xMax - xMin) / pageWidth) * 100.0F;
            float height = ((yMax - yMin) / pageHeight) * 100.0F;
            float x = (xMin / pageWidth) * 100.0F;
            float y = ((yMin / pageHeight) * 100.0F) - height * 2.0F;
            textBlockListOut.add(new TextBlock(text, x, y, width, height * 2.0F, fontSize, pageWidth, pageHeight));
        }
    }
}