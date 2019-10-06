package com.dude.dms.backend.brain;

public enum OptionKey {

    DOC_PATH("doc_path"),
    AUTO_REVIEW_TAG("auto_review_tag"),
    REVIEW_TAG_ID("review_tag_id"),
    CRUD_VIEW_SPLITTER_POS("crud_view_splitter_pos"),
    DATE_FORMAT("date_format"),
    DATE_SCAN_FORMATS("date_scan_formats"),
    LOCALE("locale");

    public final String key;

    OptionKey(String key) {
        this.key = key;
    }
}