package com.dude.dms.backend.brain

import com.dude.dms.backend.data.LogEntry
import com.dude.dms.backend.service.LogEntryService
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
        trySave(LogEntry(LocalDateTime.now(), clazz.simpleName, clazz.getPackage().toString(), msg, ui, lvl))
    }

    private fun save(lvl: Level, msg: String, e: Exception, ui: Boolean = false) {
        trySave(LogEntry(LocalDateTime.now(), clazz.simpleName, clazz.getPackage().toString(), msg, e.stackTrace.joinToString("\n") { it.toString() }, ui, lvl))
    }

    private fun trySave(logEntry: LogEntry) {
        if (logEntryService == null) {
            logEntryService = SpringContext.getBean(LogEntryService::class.java)
            if (logEntryService == null) {
                logger.warn("Logservice can not be found!")
                return
            } else {
                logger.info("Logservice restored.")
            }
        }
        logEntryService!!.save(logEntry)
    }

    fun info(message: String) {
        logger.info(message)
        save(Level.INFO, message)
    }

    fun info(message: String, vararg arguments: Any?) {
        logger.info(message, *arguments)
        save(Level.INFO, format(message, *arguments))
    }

    @JvmOverloads
    fun showInfo(message: String, persistent: Boolean = false) {
        try {
            val notification = create(message, persistent)
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS)
            notification.open()
        } catch (ignored: IllegalStateException) {
        }
        logger.info(message)
        save(Level.INFO, message, true)
    }

    fun warn(msg: String) {
        logger.warn(msg)
        save(Level.WARN, msg)
    }

    fun warn(msg: String, vararg arguments: Any?) {
        logger.warn(msg, *arguments)
        save(Level.WARN, format(msg, *arguments))
    }

    fun error(msg: String) {
        logger.error(msg)
        save(Level.ERROR, msg)
    }

    fun error(msg: String, vararg arguments: Any?) {
        logger.error(msg, *arguments)
        save(Level.ERROR, format(msg, *arguments))
    }

    fun error(msg: String, e: Exception) {
        logger.error(msg, e)
        save(Level.ERROR, msg, e)
    }

    fun error(msg: String, e: Exception, vararg arguments: Any?) {
        logger.error(msg, e)
        save(Level.ERROR, format(msg, *arguments), e)
    }

    @JvmOverloads
    fun showError(message: String, persistent: Boolean = false) {
        try {
            val notification = create(message, persistent)
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR)
            notification.open()
        } catch (ignored: IllegalStateException) {
        }
        logger.error(message)
        save(Level.ERROR, message, true)
    }

    private fun format(msg: String, vararg arguments: Any?): String {
        var formattedMsg = msg
        for (argument in arguments) {
            try {
                formattedMsg = formattedMsg.replaceFirst("\\{}".toRegex(), argument.toString())
            } catch (ignored: IllegalArgumentException) {
            }
        }
        return formattedMsg
    }

    companion object {
        @JvmStatic
        fun getLogger(clazz: Class<*>): DmsLogger {
            return DmsLogger(clazz)
        }

        private fun create(message: String, persistent: Boolean) = Notification(message, if (persistent) 0 else 3000, Notification.Position.valueOf(OptionKey.NOTIFY_POSITION.string))
    }
}