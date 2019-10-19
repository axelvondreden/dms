package com.dude.dms.updater;

import java.util.Objects;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {

    private static final Pattern PATTERN = Pattern.compile("[0-9]+(\\.[0-9]+)*");

    private String version;

    public String get() {
        return version;
    }

    public Version(String version) {
        if (version == null) {
            throw new IllegalArgumentException("Version can not be null");
        }

        this.version = version;

        if (!PATTERN.matcher(version).matches()) {
            // ugly fix for old version pattern (v0.0.1, ...)
            this.version = version.substring(1);
            if (!PATTERN.matcher(this.version).matches()) {
                throw new IllegalArgumentException("Invalid version format");
            }
        }
    }

    public boolean isBefore(Version o) {
        return compareTo(o) < 0;
    }

    public boolean isAfter(Version o) {
        return compareTo(o) > 0;
    }

    @Override
    public int compareTo(Version o) {
        if (o == null) {
            return 1;
        }
        String[] thisParts = get().split("\\.");
        String[] thatParts = o.get().split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for (int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;
            if (thisPart < thatPart) {
                return -1;
            }
            if (thisPart > thatPart) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return compareTo((Version) obj) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(version);
    }
}