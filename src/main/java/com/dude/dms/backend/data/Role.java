package com.dude.dms.backend.data;

public class Role {
    public static final String READ_ONLY = "read only";
    public static final String READ_WRITE = "read write";
    public static final String ADMIN = "admin";

    private Role() {
        // Static methods and fields only
    }

    public static String[] getAllRoles() {
        return new String[] { READ_ONLY, READ_WRITE, ADMIN };
    }

}
