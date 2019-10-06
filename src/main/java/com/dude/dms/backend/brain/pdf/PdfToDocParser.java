package com.dude.dms.backend.brain.pdf;

import com.dude.dms.backend.brain.BrainUtils;
import com.dude.dms.backend.data.entity.Doc;
import com.dude.dms.backend.data.entity.Tag;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static com.dude.dms.backend.brain.OptionKey.*;

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
        LOGGER.info("Parsing file {}...", file.getName());
        String rawText = null;
        try (PDDocument pdDoc = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            LOGGER.info("Stripping text...");
            rawText = pdfStripper.getText(pdDoc);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        Doc doc = new Doc(UUID.randomUUID().toString());
        Set<Tag> tags = new HashSet<>();
        if (Boolean.parseBoolean(BrainUtils.getProperty(AUTO_REVIEW_TAG))) {
            tagService.findById(Long.parseLong(BrainUtils.getProperty(REVIEW_TAG_ID))).ifPresent(tag -> {
                LOGGER.info("Adding tag: {}", tag.getName());
                tags.add(tag);
            });
        }

        if (rawText != null && !rawText.isEmpty()) {
            doc.setRawText(rawText);
            doc.setDocumentDate(discoverDates(rawText));
        }

        doc.setTags(tags);
        docService.create(doc);
        LOGGER.info("Created doc with ID: {}", doc.getGuid());
    }

    private static LocalDate discoverDates(String rawText) {
        LOGGER.info("Trying to find date...");
        String[] datePatterns = BrainUtils.getProperty(DATE_SCAN_FORMATS).split(",");
        Map<LocalDate, Integer> map = new HashMap<>();
        for (String pattern : datePatterns) {
            LOGGER.info("Testing pattern: {}...", pattern);
            int count = 0;
            for (String line : rawText.split("\n")) {
                for (int i = 0; i < (line.length() - pattern.length()); i++) {
                    String snippet = line.substring(i, i + pattern.length());
                    try {
                        LocalDate date = LocalDate.parse(snippet, DateTimeFormatter.ofPattern(pattern));
                        map.put(date, map.getOrDefault(date, 0) + 1);
                        count++;
                    } catch (DateTimeParseException ignored) {
                    }
                }
            }
            LOGGER.info("...Found {}", count);
        }
        Optional<Map.Entry<LocalDate, Integer>> entry = map.entrySet().stream().max(Map.Entry.comparingByValue());
        if (entry.isPresent()) {
            LOGGER.info("Setting date as {} with {} occurences", entry.get().getKey(), entry.get().getValue());
            return entry.get().getKey();
        }
        LOGGER.info("No date found on document");
        return null;
    }
}