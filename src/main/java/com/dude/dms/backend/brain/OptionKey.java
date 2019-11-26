package com.dude.dms.backend.brain;

public enum OptionKey {

    AUTO_TAG("auto_tag"),
    AUTO_TAG_ID("auto_tag_id"),
    DARK_MODE("dark_mode"),
    DATE_FORMAT("date_format"),
    DATE_SCAN_FORMATS("date_scan_formats"),
    DOC_POLL_PATH("doc_poll_path"),
    DOC_SAVE_PATH("doc_save_path"),
    DOC_SAVE_TYPE("doc_poll_type"),
    DEMO_DOCS("demo_docs"),
    FTP_URL("ftp_url"),
    FTP_USER("ftp_user"),
    FTP_PASSWORD("ftp_password"),
    FTP_PORT("ftp_port"),
    GITHUB_PASSWORD("github_password"),
    GITHUB_USER("github_user"),
    IMAGE_PARSER_DPI("image_parser_dpi"),
    LOCALE("locale"),
    MAX_UPLOAD_FILE_SIZE("max_upload_file_size"),
    NOTIFY_POSITION("notify_position"),
    POLL_INTERVAL("poll_interval"),
    SIMPLE_TAG_COLORS("simple_tag_colors"),
    UPDATE_CHECK_INTERVAL("update_check_interval");

    public final String key;

    OptionKey(String key) {
        this.key = key;
    }

    public String getString() {
        return BrainUtils.getProperty(this);
    }

    public float getFloat() {
        return Float.parseFloat(BrainUtils.getProperty(this));
    }

    public double getDouble() {
        return Double.parseDouble(BrainUtils.getProperty(this));
    }

    public int getInt() {
        return (int) Float.parseFloat(BrainUtils.getProperty(this));
    }

    public boolean getBoolean() {
        return Boolean.parseBoolean(BrainUtils.getProperty(this));
    }

    public long getLong() {
        return (long) Float.parseFloat(BrainUtils.getProperty(this));
    }

    public void setString(String value) {
        BrainUtils.setProperty(this, value);
    }

    public void setFloat(float value) {
        BrainUtils.setProperty(this, String.valueOf(value));
    }

    public void setDouble(double value) {
        BrainUtils.setProperty(this, String.valueOf(value));
    }

    public void setInt(int value) {
        BrainUtils.setProperty(this, String.valueOf(value));
    }

    public void setBoolean(boolean value) {
        BrainUtils.setProperty(this, String.valueOf(value));
    }

    public void setLong(long value) {
        BrainUtils.setProperty(this, String.valueOf(value));
    }
}