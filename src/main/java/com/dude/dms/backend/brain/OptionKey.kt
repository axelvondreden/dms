package com.dude.dms.backend.brain

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

enum class OptionKey(val key: String) {

    AUTO_TAG("auto_tag"),
    AUTO_TAG_ID("auto_tag_id"),
    BACKUP_METHOD("backup_method"),
    DARK_MODE("dark_mode"),
    DATE_FORMAT("date_format"),
    DATE_SCAN_FORMATS("date_scan_formats"),
    DOC_POLL_PATH("doc_poll_path"),
    DOC_SAVE_PATH("doc_save_path"),
    DEMO_DOCS("demo_docs"),
    FTP_URL("ftp_url"),
    FTP_USER("ftp_user"),
    FTP_PASSWORD("ftp_password"),
    FTP_PORT("ftp_port"),
    IMAGE_PARSER_DPI("image_parser_dpi"),
    IMAP_HOST("imap_host"),
    IMAP_PORT("imap_port"),
    IMAP_LOGIN("imap_login"),
    IMAP_PASSWORD("imap_password"),
    LOCALE("locale"),
    MAX_UPLOAD_FILE_SIZE("max_upload_file_size"),
    NOTIFY_POSITION("notify_position"),
    POLL_INTERVAL("poll_interval"),
    SIMPLE_TAG_COLORS("simple_tag_colors"),
    UPDATE_CHECK_INTERVAL("update_check_interval");

    var string
        get() = getProperty(this) ?: ""
        set(value) {
            setProperty(this, value)
        }

    var float
        get() = if (string.isNotEmpty()) string.toFloat() else 0.0F
        set(value) {
            setProperty(this, value.toString())
        }

    var double
        get() = if (string.isNotEmpty()) string.toDouble() else 0.0
        set(value) {
            setProperty(this, value.toString())
        }

    var int
        get() = if (string.isNotEmpty()) string.toDouble().toInt() else 0
        set(value) {
            setProperty(this, value.toString())
        }

    var boolean
        get() = if (string.isNotEmpty()) string.toBoolean() else false
        set(value) {
            setProperty(this, value.toString())
        }

    var long
        get() = if (string.isNotEmpty()) string.toDouble().toLong() else 0L
        set(value) {
            setProperty(this, value.toString())
        }

    fun getProperty(key: OptionKey) = getUserProperty(key) ?: getDefaultProperty(key)

    fun setProperty(key: OptionKey, value: String?) {
        val prop = getProperties("options.properties")
        prop.setProperty(key.key, value)
        try {
            FileOutputStream(File("options.properties")).use { output -> prop.store(output, null) }
        } catch (io: IOException) {
            io.message?.let { LOGGER.error(it) }
        }
    }

    private fun getDefaultProperty(key: OptionKey) = getProperties("options.default.properties").getProperty(key.key)

    private fun getUserProperty(key: OptionKey) = getProperties("options.properties").getProperty(key.key)

    private fun getProperties(file: String): Properties {
        val prop = Properties()
        try {
            FileInputStream(File(file)).use { input -> prop.load(input) }
        } catch (ex: IOException) {
            ex.message?.let { LOGGER.error(it) }
        }
        return prop
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(OptionKey::class.java)
    }
}