package com.dude.dms.backend.brain;

import java.io.*;
import java.util.Properties;

public final class BrainUtils {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(BrainUtils.class);

    private BrainUtils() {
    }

    public static String getProperty(OptionKey key) {
        String userProperty = getUserProperty(key);
        return userProperty != null ? userProperty : getDefaultProperty(key);
    }

    public static void setProperty(OptionKey key, String value) {
        Properties prop = getUserProperties();
        prop.setProperty(key.key, value);
        try (OutputStream output = new FileOutputStream(new File("options.properties"))) {
            prop.store(output, null);
        } catch (IOException io) {
            LOGGER.error(io.getMessage());
        }
    }

    private static String getDefaultProperty(OptionKey key) {
        return getDefaultProperties().getProperty(key.key);
    }

    private static String getUserProperty(OptionKey key) {
        return getUserProperties().getProperty(key.key);
    }

    private static Properties getUserProperties() {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream(new File("options.properties"))) {
            prop.load(input);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
        return prop;
    }

    private static Properties getDefaultProperties() {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream(new File("options.default.properties"))) {
            prop.load(input);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
        return prop;
    }
}