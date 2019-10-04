package com.dude.dms.backend.brain.pdf;

import com.dude.dms.backend.data.entity.Doc;
import com.dude.dms.backend.service.DocService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class PdfToDocParser {

    @Autowired
    private DocService docService;

    /**
     * Creates a new {@link Doc} from a file.
     *
     * @param file the file used for parsing, this has a to be a pdf file.
     */
    public void parse(File file) {
        try {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            PDDocument pdDoc = PDDocument.load(file);

            Doc doc = new Doc(file.getName(), pdfStripper.getText(pdDoc), UUID.randomUUID().toString());
            docService.create(doc);

            pdDoc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}