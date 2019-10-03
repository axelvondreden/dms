package com.dude.dms.backend.brain;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class BrainUtils {

    private BrainUtils() {

    }

    public static String getProperty(String key) {
        try (InputStream input = new FileInputStream("src\\main\\resources\\brain.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty(key);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
