package com.dude.dms;

import com.dude.dms.backend.brain.BrainUtils;
import com.dude.dms.backend.data.base.Tag;
import com.dude.dms.backend.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import static com.dude.dms.backend.brain.OptionKey.*;

@Component
public class StartUpRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartUpRunner.class);

    @Autowired
    private TagService tagService;

    @Override
    public void run(String... args) throws IOException {
        createOptionsFile();
        checkOptions();
        createTags();
    }

    private static void checkOptions() throws IOException {
        File scanDir = new File(BrainUtils.getProperty(DOC_PATH));
        if (!scanDir.exists() || !scanDir.isDirectory()) {
            throw new IOException("Scan directory must be specified in 'option.properties'");
        }
    }

    private void createTags() {
        if (Boolean.parseBoolean(BrainUtils.getProperty(AUTO_REVIEW_TAG))) {
            Tag reviewTag = tagService.create(new Tag("review", "red"));
            BrainUtils.setProperty(REVIEW_TAG_ID, String.valueOf(reviewTag.getId()));
        }
    }

    private static void createOptionsFile() throws IOException {
        File prop = new File("src/main/resources/options.properties");
        if (!prop.exists()) {
            LOGGER.info("Creating user properties...");
            prop.createNewFile();
        }
    }
}