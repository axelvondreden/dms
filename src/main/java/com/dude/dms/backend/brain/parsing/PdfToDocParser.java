package com.dude.dms.backend.brain.parsing;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.docs.TextBlock;
import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.data.tags.Tag;
import com.dude.dms.backend.service.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.EOFException;
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
    private TextBlockService textBlockService;

    @Autowired
    private PlainTextRuleService plainTextRuleService;

    @Autowired
    private RegexRuleService regexRuleService;

    private final String docSavePath;

    private List<TextBlock> textBlockList;

    private final Map<String, ParserEventListener> eventListeners;

    public PdfToDocParser() {
        docSavePath = DOC_SAVE_PATH.getString();
        eventListeners = new HashMap<>();
    }

    /**
     * Creates a new {@link Doc} from a file.
     *
     * @param file the file used for parsing, this has a to be a pdf file.
     */
    public void parse(File file) {
        LOGGER.info("Parsing file {}...", file.getName());
        String guid = UUID.randomUUID().toString();
        String rawText = null;

        Optional<File> savedFile = savePdf(file, guid);
        if (savedFile.isPresent()) {
            try (PDDocument pdDoc = PDDocument.load(savedFile.get())) {
                saveImage(pdDoc, guid);
                rawText = stripText(pdDoc);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }

            Doc doc = new Doc(guid);

            if (rawText != null && !rawText.isEmpty()) {
                doc.setRawText(rawText);
                doc.setDocumentDate(discoverDates(rawText));
            }

            doc.setTags(discoverTags(rawText));

            docService.create(doc);
            LOGGER.info("Created doc with ID: {}", doc.getGuid());

            if (textBlockList != null) {
                textBlockList.forEach(textBlock -> {
                    textBlock.setDoc(doc);
                    textBlockService.create(textBlock);
                    LOGGER.info("Created textblock {} for doc {}", textBlock, doc);
                });
            }
            if (eventListeners != null) {
                eventListeners.values().forEach(listener -> listener.onParse(true));
            }
        } else {
            LOGGER.error("Could not save file {}", file.getAbsolutePath());
            if (eventListeners != null) {
                eventListeners.values().forEach(listener -> listener.onParse(false));
            }
        }
    }

    /**
     * Tries to find tags for a doc by all available methods
     *
     * @param rawText raw text of the document
     * @return list of tags
     */
    private Set<Tag> discoverTags(String rawText) {
        Set<Tag> tags = discoverAutoTags();
        if (rawText != null && !rawText.isEmpty()) {
            tags.addAll(discoverRuleTags(rawText));
        }
        return tags;
    }

    /**
     * Validates all active rules against the raw text and adds tags for matching rules.
     *
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
     *
     * @return list of tags
     */
    private Set<Tag> discoverAutoTags() {
        Set<Tag> tags = new HashSet<>();
        if (AUTO_REVIEW_TAG.getBoolean()) {
            tagService.findById(REVIEW_TAG_ID.getLong()).ifPresent(tag -> {
                LOGGER.info("Adding tag: {}", tag.getName());
                tags.add(tag);
            });
        }
        return tags;
    }

    private String stripText(PDDocument pdDoc) throws IOException {
        DmsPdfTextStripper pdfStripper = new DmsPdfTextStripper();
        LOGGER.info("Stripping text...");
        textBlockList = new ArrayList<>();
        return pdfStripper.getTextWithPositions(pdDoc, textBlockList);
    }

    private Optional<File> savePdf(File file, String guid) {
        Path targetPath = Paths.get(docSavePath, "pdf", guid + ".pdf");
        LOGGER.info("Saving PDF {}...", targetPath);
        try {
            return Optional.of(Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING).toFile());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return Optional.empty();
        }
    }

    private void saveImage(PDDocument pdDoc, String guid) throws IOException {
        PDFRenderer pr = new PDFRenderer(pdDoc);
        for (int i = 0; i < pdDoc.getNumberOfPages(); i++) {
            try {
                BufferedImage bi = pr.renderImageWithDPI(i, IMAGE_PARSER_DPI.getFloat());
                File out = new File(docSavePath, String.format("img/%s_%02d.png", guid, i));
                LOGGER.info("Saving Image {}...", out.getAbsolutePath());
                ImageIO.write(bi, "PNG", out);
            } catch (EOFException e) {
                LOGGER.error("Error when saving image: EOFException {}", e.getMessage());
            }
        }
    }

    private static LocalDate discoverDates(String rawText) {
        LOGGER.info("Trying to find date...");
        String[] datePatterns = DATE_SCAN_FORMATS.getString().split(",");
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

    public void addEventListener(String key, ParserEventListener eventListener) {
        eventListeners.put(key, eventListener);
    }
}