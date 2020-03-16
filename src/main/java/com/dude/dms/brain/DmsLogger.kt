package com.dude.dms.brain

import com.dude.dms.backend.data.LogEntry
import com.dude.dms.backend.service.LogEntryService
import com.dude.dms.brain.options.Options
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class DmsLogger private constructor(private val clazz: Class<*>) {

    private val logger = LoggerFactory.getLogger(clazz)

    private var logEntryService = SpringContext.getBean(LogEntryService::class.java)

    enum class Level {
        INFO, WARN, ERROR
    }

    private fun save(lvl: Level, msg: String, ui: Boolean = false) {
        trySave(LogEntry(LocalDateTime.now(), clazz.simpleName, clazz.getPackage().toString(), msg, lvl, isUi = ui))
    }

    private fun save(lvl: Level, msg: String, e: Exception, ui: Boolean = false) {
        trySave(LogEntry(LocalDateTime.now(), clazz.simpleName, clazz.getPackage().toString(), msg, lvl, e.stackTrace.joinToString("\n") { it.toString() }, ui))
    }

    private fun trySave(logEntry: LogEntry) {
        if (logEntryService == null) {
            logEntryService = SpringContext.getBean(LogEntryService::class.java)
            if (logEntryService == null) {
                logger.warn(t("log.missing"))
                return
            } else {
                logger.info("log.restored")
            }
        }
        logEntryService!!.save(logEntry)
    }

    fun info(message: String) {
        logger.info(message)
        save(Level.INFO, message)
    }

    fun showInfo(message: String, ui: UI, persistent: Boolean = false, log: Boolean = true) {
        try {
            val notification = create(message, persistent)
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS)
            ui.access { notification.open() }
        } catch (e: IllegalStateException) {
            logger.warn(t("log.error.ui", e))
        }
        if (log) {
            logger.info(message)
            save(Level.INFO, message, true)
        }
    }

    fun warn(msg: String) {
        logger.warn(msg)
        save(Level.WARN, msg)
    }

    fun error(msg: String) {
        logger.error(msg)
        save(Level.ERROR, msg)
    }

    fun error(msg: String, e: Exception) {
        logger.error(msg, e)
        save(Level.ERROR, msg, e)
    }

    fun showError(message: String, ui: UI, persistent: Boolean = false, log: Boolean = true) {
        try {
            val notification = create(message, persistent)
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR)
            ui.access { notification.open() }
        } catch (e: IllegalStateException) {
            logger.warn(t("log.error.ui", e))
        }
        if (log) {
            logger.error(message)
            save(Level.ERROR, message, true)
        }
    }

    private fun create(message: String, persistent: Boolean): Notification {
        return Notification(message, if (persistent) 0 else 3000, Notification.Position.valueOf(Options.get().view.notificationPosition))
    }

    companion object {
        @JvmStatic
        fun getLogger(clazz: Class<*>) = DmsLogger(clazz)
    }
}