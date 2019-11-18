package com.dude.dms.backend.brain;

import com.dude.dms.backend.data.LogEntry;
import com.dude.dms.backend.service.LogEntryService;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
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
        save(lvl, msg, false);
    }

    private void save(Level lvl, String msg, boolean ui) {
        trySave(new LogEntry(LocalDateTime.now(), clazz.getSimpleName(), clazz.getPackage().toString(), msg, ui, lvl));
    }

    private void save(Level lvl, String msg, Exception e) {
        save(lvl, msg, e, false);
    }

    private void save(Level lvl, String msg, Exception e, boolean ui) {
        trySave(new LogEntry(LocalDateTime.now(), clazz.getSimpleName(), clazz.getPackage().toString(), msg, Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n")), ui, lvl));
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

    public void info(String message) {
        logger.info(message);
        save(Level.INFO, message);
    }

    public void info(String message, Object... arguments) {
        logger.info(message, arguments);
        save(Level.INFO, format(message, arguments));
    }

    public void showInfo(String message) {
        showInfo(message, false);
    }

    public void showInfo(String message, boolean persistent) {
        try {
            Notification notification = create(message, persistent);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.open();
        } catch (IllegalStateException ignored) {
        }
        logger.info(message);
        save(Level.INFO, message, true);
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

    public void showError(String message) {
        showError(message, false);
    }

    public void showError(String message, boolean persistent) {
        try {
            Notification notification = create(message, persistent);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
        } catch (IllegalStateException ignored) {
        }
        logger.error(message);
        save(Level.ERROR, message, true);
    }

    private static Notification create(String message, boolean persistent) {
        return new Notification(message, persistent ? 0 : 3000, Notification.Position.valueOf(OptionKey.NOTIFY_POSITION.getString()));
    }

    private String format(String msg, Object... arguments) {
        String formattedMsg = msg;
        for (Object argument : arguments) {
            try {
                formattedMsg = formattedMsg.replaceFirst("\\{}", argument.toString());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return formattedMsg;
    }
}