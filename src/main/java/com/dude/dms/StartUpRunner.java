package com.dude.dms;

import com.dude.dms.backend.brain.BrainUtils;
import com.dude.dms.backend.brain.polling.DocPollingService;
import com.dude.dms.backend.data.base.Tag;
import com.dude.dms.backend.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

import static com.dude.dms.backend.brain.OptionKey.*;

@Component
public class StartUpRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartUpRunner.class);

    @Autowired
    private TagService tagService;

    @Autowired
    private DocPollingService docPollingService;

    private Random random;

    @Override
    public void run(String... args) throws IOException {
        createOptionsFile();
        checkOptions();
        createTags();
        docPollingService.manualPoll();
        LocaleContextHolder.setLocale(Locale.forLanguageTag(BrainUtils.getProperty(LOCALE)));

        createDemoData();
    }

    private static void checkOptions() {
        File pollDir = new File(BrainUtils.getProperty(DOC_POLL_PATH));
        if (!pollDir.exists()) {
            LOGGER.info("Creating input directory new docs {}", pollDir);
            pollDir.mkdir();
        }
        File saveDir = new File(BrainUtils.getProperty(DOC_SAVE_PATH));
        if (!saveDir.exists()) {
            LOGGER.info("Creating directory for saved docs {}", saveDir);
            saveDir.mkdir();
        }
    }

    private void createTags() {
        if (Boolean.parseBoolean(BrainUtils.getProperty(AUTO_REVIEW_TAG))) {
            Tag reviewTag = tagService.create(new Tag("Review", "red"));
            BrainUtils.setProperty(REVIEW_TAG_ID, String.valueOf(reviewTag.getId()));
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
        tagService.create(new Tag("Rechnung", randomColor()));
        tagService.create(new Tag("Auto", randomColor()));
        tagService.create(new Tag("Beleg", randomColor()));
        tagService.create(new Tag("Test", randomColor()));
        tagService.create(new Tag("Test 2", randomColor()));
    }

    private String randomColor() {
        return String.format("#%06x", random.nextInt(0xffffff + 1));
    }
}