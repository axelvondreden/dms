package com.dude.dms.backend.brain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DmsLogger {

    private final Logger logger;
    private final Class<?> clazz;

    public enum Level {
        INFO, WARN, ERROR
    }

    private DmsLogger(Class<?> clazz) {
        this.clazz = clazz;
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public static DmsLogger getLogger(Class<?> clazz) {
        return new DmsLogger(clazz);
    }

    private void save(Level lvl, String msg) {

    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void info() {
        //logger.inf;
    }
}
