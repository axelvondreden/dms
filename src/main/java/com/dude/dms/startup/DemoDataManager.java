package com.dude.dms.startup;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.data.docs.Attribute;
import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.service.AttributeService;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.dude.dms.backend.brain.OptionKey.*;

@Component
public class DemoDataManager {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(DemoDataManager.class);

    @Autowired
    private TagService tagService;

    @Autowired
    private DocService docService;

    @Autowired
    private AttributeService attributeService;

    private Random random;

    public void createDemoData() {
        random = new SecureRandom();

        createReviewTag();

        Set<Tag> tags = createDemoTags();

        if (docService.count() == 0L && DEMO_DOCS.getInt() > 0) {
            LOGGER.info("Creating demo docs...");
            createDemoDocs(tags);
        }
    }

    private void createDemoDocs(Iterable<Tag> tags) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get("lipsum.txt"), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append(' '));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        String txt = contentBuilder.toString();
        for (int i = 0; i < DEMO_DOCS.getInt(); i++) {
            Set<Tag> rngTags = new HashSet<>();
            for (Tag tag : tags) {
                if (random.nextFloat() > 0.7F) {
                    rngTags.add(tag);
                }
            }
            LocalDate date = LocalDate.of(2016 + random.nextInt(4), 1 + random.nextInt(12), 1 + random.nextInt(28));
            int r1 = random.nextInt(txt.length());
            int r2 = r1 + Math.min(random.nextInt(txt.length() - r1), 5000);
            docService.create(new Doc(date, txt.substring(r1, r2), UUID.randomUUID().toString(), rngTags));
        }
    }

    private Set<Tag> createDemoTags() {
        Set<Tag> tags = new HashSet<>();
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(attributeService.findByName("Firma").orElse(attributeService.create(new Attribute("Firma", false, Attribute.Type.STRING))));
        attributes.add(attributeService.findByName("Betrag").orElse(attributeService.create(new Attribute("Betrag", false, Attribute.Type.FLOAT))));
        attributes.add(attributeService.findByName("Garantie").orElse(attributeService.create(new Attribute("Garantie", false, Attribute.Type.DATE))));
        tags.add(tagService.create(new Tag("Rechnung", randomColor(), attributes)));
        tags.add(tagService.create(new Tag("Auto", randomColor())));
        tags.add(tagService.create(new Tag("Beleg", randomColor())));
        tags.add(tagService.create(new Tag("Einkauf", randomColor())));
        tags.add(tagService.create(new Tag("Steuer", randomColor())));
        tags.add(tagService.create(new Tag("Arbeit", randomColor())));
        tags.add(tagService.create(new Tag("Test", randomColor())));
        return tags;
    }

    private String randomColor() {
        return String.format("#%06x", random.nextInt(0xffffff + 1));
    }

    private void createReviewTag() {
        if (AUTO_TAG.getBoolean()) {
            Tag reviewTag = tagService.create(new Tag("Review", "red"));
            AUTO_TAG_ID.setFloat(reviewTag.getId());
        }
    }
}