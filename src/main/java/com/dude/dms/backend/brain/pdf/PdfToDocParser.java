package com.dude.dms.backend.brain.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class PdfToDocParser {

    public void parse(File file) throws IOException {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        PDDocument pdDoc = PDDocument.load(file);
        String s = pdfStripper.getText(pdDoc);
    }
}