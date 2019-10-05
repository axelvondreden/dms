package com.dude.dms.backend.brain.pdf;

import com.dude.dms.backend.brain.BrainUtils;
import com.dude.dms.backend.data.entity.Doc;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.dude.dms.backend.brain.OptionKey.AUTO_REVIEW_TAG;
import static com.dude.dms.backend.brain.OptionKey.REVIEW_TAG_ID;

@Component
public class PdfToDocParser implements Parser {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfToDocParser.class);

    @Autowired
    private DocService docService;

    @Autowired
    private TagService tagService;

    /**
     * Creates a new {@link Doc} from a file.
     *
     * @param file the file used for parsing, this has a to be a pdf file.
     */
    public void parse(File file) {
        LOGGER.debug("Parsing file {}", file.getName());
        try (PDDocument pdDoc = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();

            Doc doc = docService.create(new Doc(file.getName(), pdfStripper.getText(pdDoc), UUID.randomUUID().toString()));
            if (Boolean.parseBoolean(BrainUtils.getProperty(AUTO_REVIEW_TAG))) {
                tagService.findById(Long.parseLong(BrainUtils.getProperty(REVIEW_TAG_ID))).ifPresent(tag -> {
                    doc.getTags().add(tag);
                    docService.save(doc);
                });
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}