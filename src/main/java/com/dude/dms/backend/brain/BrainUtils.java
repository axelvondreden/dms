package com.dude.dms.backend.brain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class BrainUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrainUtils.class);

    private BrainUtils() {

    }

    public static String getProperty(String key) {
        LOGGER.debug("Retrieving property for key: '{}'", key);
        try (InputStream input = new FileInputStream("src/main/resources/brain.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty(key);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            return null;
        }
    }
}