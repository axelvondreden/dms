package com.dude.dms.backend.brain

import com.dude.dms.backend.brain.BrainUtils.getProperty
import com.dude.dms.backend.brain.BrainUtils.setProperty

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
        get() = getProperty(this)?.toFloat() ?: 0.0F
        set(value) {
            setProperty(this, value.toString())
        }

    var double
        get() = getProperty(this)?.toDouble() ?: 0.0
        set(value) {
            setProperty(this, value.toString())
        }

    var int
        get() = getProperty(this)?.toFloat()?.toInt() ?: 0
        set(value) {
            setProperty(this, value.toString())
        }

    var boolean
        get() = getProperty(this)?.toBoolean() ?: false
        set(value) {
            setProperty(this, value.toString())
        }

    var long
        get() = getProperty(this)?.toFloat()?.toLong() ?: 0
        set(value) {
            setProperty(this, value.toString())
        }
}