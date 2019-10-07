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
import java.util.Locale;

import static com.dude.dms.backend.brain.OptionKey.*;

@Component
public class StartUpRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartUpRunner.class);

    @Autowired
    private TagService tagService;

    @Autowired
    private DocPollingService docPollingService;

    @Override
    public void run(String... args) throws IOException {
        createOptionsFile();
        checkOptions();
        createTags();
        docPollingService.manualPoll();
        LocaleContextHolder.setLocale(Locale.forLanguageTag(BrainUtils.getProperty(LOCALE)));
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
            Tag reviewTag = tagService.create(new Tag("review", "red"));
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
}