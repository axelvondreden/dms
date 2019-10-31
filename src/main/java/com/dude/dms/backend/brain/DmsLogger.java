package com.dude.dms.backend.brain;

import com.dude.dms.backend.data.LogEntry;
import com.dude.dms.backend.service.LogEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DmsLogger {

    private final Logger logger;
    private final Class<?> clazz;
    private LogEntryService logEntryService;

    public enum Level {
        INFO, WARN, ERROR
    }

    private DmsLogger(Class<?> clazz) {
        this.clazz = clazz;
        this.logger = LoggerFactory.getLogger(clazz);
        logEntryService = SpringContext.getBean(LogEntryService.class);
    }

    public static DmsLogger getLogger(Class<?> clazz) {
        return new DmsLogger(clazz);
    }

    private void save(Level lvl, String msg) {
        trySave(new LogEntry(LocalDateTime.now(), clazz.getSimpleName(), clazz.getPackage().toString(), msg, lvl));
    }

    private void save(Level lvl, String msg, Exception e) {
        trySave(new LogEntry(LocalDateTime.now(), clazz.getSimpleName(), clazz.getPackage().toString(), msg, Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n")), lvl));
    }

    private void trySave(LogEntry logEntry) {
        if (logEntryService == null) {
            logEntryService = SpringContext.getBean(LogEntryService.class);
            if (logEntryService == null) {
                logger.warn("Logservice can not be found!");
                return;
            } else {
                logger.info("Logservice restored.");
            }
        }
        logEntryService.save(logEntry);
    }

    public void info(String msg) {
        logger.info(msg);
        save(Level.INFO, msg);
    }

    public void info(String msg, Object... arguments) {
        logger.info(msg, arguments);
        save(Level.INFO, format(msg, arguments));
    }

    public void warn(String msg) {
        logger.warn(msg);
        save(Level.WARN, msg);
    }

    public void warn(String msg, Object... arguments) {
        logger.warn(msg, arguments);
        save(Level.WARN, format(msg, arguments));
    }

    public void error(String msg) {
        logger.error(msg);
        save(Level.ERROR, msg);
    }

    public void error(String msg, Object... arguments) {
        logger.error(msg, arguments);
        save(Level.ERROR, format(msg, arguments));
    }

    public void error(String msg, Exception e) {
        logger.error(msg, e);
        save(Level.ERROR, msg, e);
    }

    public void error(String msg, Exception e, Object... arguments) {
        logger.error(msg, e);
        save(Level.ERROR, format(msg, arguments), e);
    }

    private String format(String msg, Object... arguments) {
        String formattedMsg = msg;
        for (Object argument : arguments) {
            formattedMsg = formattedMsg.replaceFirst("\\{}", argument.toString());
        }
        return formattedMsg;
    }
}
