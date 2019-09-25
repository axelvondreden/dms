package com.dude.dms.ui.utils;

import org.springframework.data.domain.Sort.Direction;

import java.util.Locale;

public class Const {

    public static final Locale APP_LOCALE = Locale.US;

    public static final String PAGE_ROOT = "";
    public static final String PAGE_DOCS = "docs";
    public static final String PAGE_USERS = "users";
    public static final String PAGE_TAGS = "tags";
    public static final String PAGE_PERSONS = "persons";
    public static final String PAGE_ACCOUNTS = "accounts";

    public static final String TITLE_DOCS = "Docs";
    public static final String TITLE_USERS = "Users";
    public static final String TITLE_TAGS = "Tags";
    public static final String TITLE_PERSONS = "Persons";
    public static final String TITLE_ACCOUNTS = "Accounts";
    public static final String TITLE_LOGOUT = "Logout";
    public static final String TITLE_NOT_FOUND = "Page was not found";

    public static final String[] DOC_SORT_FIELDS = { "uploadDate", "orderId" };
    public static final Direction DEFAULT_SORT_DIRECTION = Direction.ASC;

    public static final String VIEWPORT = "width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes, viewport-fit=cover";

    public static final int NOTIFICATION_DURATION = 4000;

    private Const() {
    }
}
