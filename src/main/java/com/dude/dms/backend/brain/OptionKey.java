package com.dude.dms.backend.brain;

public enum OptionKey {

    DOC_POLL_PATH("doc_poll_path"),
    DOC_SAVE_PATH("doc_save_path"),
    AUTO_REVIEW_TAG("auto_review_tag"),
    REVIEW_TAG_ID("review_tag_id"),
    DATE_FORMAT("date_format"),
    DATE_SCAN_FORMATS("date_scan_formats"),
    LOCALE("locale");

    public final String key;

    OptionKey(String key) {
        this.key = key;
    }
}