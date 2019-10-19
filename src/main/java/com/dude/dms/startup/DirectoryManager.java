package com.dude.dms.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.dude.dms.backend.brain.OptionKey.DOC_POLL_PATH;
import static com.dude.dms.backend.brain.OptionKey.DOC_SAVE_PATH;

@Component
class DirectoryManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryManager.class);

    public void checkDirectories() {
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
}