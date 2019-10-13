package com.dude.dms;

import com.dude.dms.backend.brain.polling.DocPollingService;
import com.dude.dms.backend.data.base.Doc;
import com.dude.dms.backend.data.base.Tag;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static com.dude.dms.backend.brain.OptionKey.*;

@Component
public class StartUpRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartUpRunner.class);

    @Autowired
    private TagService tagService;

    @Autowired
    private DocService docService;

    @Autowired
    private DocPollingService docPollingService;

    private Random random;

    @Override
    public void run(String... args) throws IOException {
        createOptionsFile();
        createDirectories();
        createTags();
        docPollingService.manualPoll();
        LocaleContextHolder.setLocale(Locale.forLanguageTag(LOCALE.getString()));

        createDemoData();
    }

    private static void createDirectories() {
        File pollDir = new File(DOC_POLL_PATH.getString());
        if (!pollDir.exists()) {
            LOGGER.info("Creating input directory new docs {}", pollDir);
            pollDir.mkdir();
        }
        File saveDir = new File(DOC_SAVE_PATH.getString());
        if (!saveDir.exists()) {
            LOGGER.info("Creating directory for saved docs {}", saveDir);
            saveDir.mkdir();
            new File(saveDir, "pdf").mkdir();
            new File(saveDir, "img").mkdir();
        }
    }

    private void createTags() {
        if (AUTO_REVIEW_TAG.getBoolean()) {
            Tag reviewTag = tagService.create(new Tag("Review", "red"));
            REVIEW_TAG_ID.setFloat(reviewTag.getId());
        }
    }

    private static void createOptionsFile() throws IOException {
        File prop = new File("options.properties");
        if (!prop.exists()) {
            LOGGER.info("Creating user properties...");
            prop.createNewFile();
        }
    }

    private void createDemoData() {
        random = new SecureRandom();
        Collection<Tag> tags = new HashSet<>();
        tags.add(tagService.create(new Tag("Rechnung", randomColor())));
        tags.add(tagService.create(new Tag("Auto", randomColor())));
        tags.add(tagService.create(new Tag("Beleg", randomColor())));
        tags.add(tagService.create(new Tag("Einkauf", randomColor())));
        tags.add(tagService.create(new Tag("Steuer", randomColor())));
        tags.add(tagService.create(new Tag("Arbeit", randomColor())));
        tags.add(tagService.create(new Tag("Test", randomColor())));

        if (docService.count() == 0L) {
            LOGGER.info("Creating demo docs...");

            StringBuilder contentBuilder = new StringBuilder();
            try (Stream<String> stream = Files.lines( Paths.get("lipsum.txt"), StandardCharsets.UTF_8)) {
                stream.forEach(s -> contentBuilder.append(s).append(' '));
            } catch (IOException e) {
                e.printStackTrace();
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
    }

    private String randomColor() {
        return String.format("#%06x", random.nextInt(0xffffff + 1));
    }
}