package com.dude.dms.backend.brain.pdf;

import com.dude.dms.backend.brain.BrainUtils;
import com.dude.dms.backend.data.base.Doc;
import com.dude.dms.backend.data.base.Tag;
import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.PlainTextRuleService;
import com.dude.dms.backend.service.RegexRuleService;
import com.dude.dms.backend.service.TagService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.dude.dms.backend.brain.OptionKey.*;

@Component
public class PdfToDocParser implements Parser {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfToDocParser.class);

    @Autowired
    private DocService docService;

    @Autowired
    private TagService tagService;

    @Autowired
    private PlainTextRuleService plainTextRuleService;

    @Autowired
    private RegexRuleService regexRuleService;

    private final String docSavePath;

    public PdfToDocParser() {
        docSavePath = BrainUtils.getProperty(DOC_SAVE_PATH);
    }

    /**
     * Creates a new {@link Doc} from a file.
     *
     * @param file the file used for parsing, this has a to be a pdf file.
     */
    public void parse(File file) {
        LOGGER.info("Parsing file {}...", file.getName());
        String guid = UUID.randomUUID().toString();
        File savedFile = saveFile(file, guid + ".pdf");

        Doc doc = new Doc(guid);

        String rawText = stripText(savedFile);
        if (rawText != null && !rawText.isEmpty()) {
            doc.setRawText(rawText);
            doc.setDocumentDate(discoverDates(rawText));
        }

        doc.setTags(discoverTags(rawText));

        docService.create(doc);
        LOGGER.info("Created doc with ID: {}", doc.getGuid());
    }

    /**
     * Tries to find tags for a doc by all available methods
     * @param rawText raw text of the document
     * @return list of tags
     */
    private Set<Tag> discoverTags(String rawText) {
        Set<Tag> tags = discoverAutoTags();
        tags.addAll(discoverRuleTags(rawText));
        return tags;
    }

    /**
     * Validates all active rules against the raw text and adds tags for matching rules.
     * @param rawText raw text
     * @return list of tags
     */
    private Set<Tag> discoverRuleTags(String rawText) {
        Set<Tag> tags = new HashSet<>();

        for (PlainTextRule rule : plainTextRuleService.getActiveRules()) {
            for (String line : rawText.split("\n")) {
                if (rule.validate(line)) {
                    Set<Tag> ruleTags = tagService.findByPlainTextRule(rule);
                    LOGGER.info("{} found a match! Adding tags[{}]...", rule, ruleTags.stream().map(Tag::getName).collect(Collectors.joining(",")));
                    tags.addAll(ruleTags);
                    break;
                }
            }
        }

        for (RegexRule rule : regexRuleService.getActiveRules()) {
            for (String line : rawText.split("\n")) {
                if (rule.validate(line)) {
                    Set<Tag> ruleTags = tagService.findByRegexRule(rule);
                    LOGGER.info("{} found a match! Adding tags[{}]...", rule, ruleTags.stream().map(Tag::getName).collect(Collectors.joining(",")));
                    tags.addAll(ruleTags);
                    break;
                }
            }
        }
        return tags;
    }

    /**
     * Gets tags, wich are set to automatic in the user_settings
     * @return list of tags
     */
    private Set<Tag> discoverAutoTags() {
        Set<Tag> tags = new HashSet<>();
        if (Boolean.parseBoolean(BrainUtils.getProperty(AUTO_REVIEW_TAG))) {
            tagService.findById(Long.parseLong(BrainUtils.getProperty(REVIEW_TAG_ID))).ifPresent(tag -> {
                LOGGER.info("Adding tag: {}", tag.getName());
                tags.add(tag);
            });
        }
        return tags;
    }

    private static String stripText(File file) {
        String rawText = null;
        try (PDDocument pdDoc = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            LOGGER.info("Stripping text...");
            rawText = pdfStripper.getText(pdDoc);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return rawText;
    }

    private File saveFile(File file, String newName) {
        Path targetPath = Paths.get(docSavePath, newName);
        LOGGER.info("Copying to {}...", targetPath);
        try {
            Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return new File(targetPath.toUri());
    }

    private static LocalDate discoverDates(String rawText) {
        LOGGER.info("Trying to find date...");
        String[] datePatterns = BrainUtils.getProperty(DATE_SCAN_FORMATS).split(",");
        Map<LocalDate, Integer> map = new HashMap<>();
        for (String pattern : datePatterns) {
            for (String line : rawText.split("\n")) {
                for (int i = 0; i < (line.length() - pattern.length()); i++) {
                    String snippet = line.substring(i, i + pattern.length());
                    try {
                        LocalDate date = LocalDate.parse(snippet, DateTimeFormatter.ofPattern(pattern));
                        map.put(date, map.getOrDefault(date, 0) + 1);
                    } catch (DateTimeParseException ignored) {
                    }
                }
            }
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