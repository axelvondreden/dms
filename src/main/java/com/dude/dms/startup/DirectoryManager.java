package com.dude.dms.startup;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.brain.FileManager;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.dude.dms.backend.brain.OptionKey.DOC_POLL_PATH;

@Component
public class DirectoryManager {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(DirectoryManager.class);

    public void checkDirectories() {
        File pollDir = new File(DOC_POLL_PATH.getString());
        if (!pollDir.exists()) {
            LOGGER.info("Creating input directory new docs {}", pollDir);
            pollDir.mkdir();
        }
        FileManager.createDirectories();
    }
}