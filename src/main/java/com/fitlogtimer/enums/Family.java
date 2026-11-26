package com.fitlogtimer.enums;

import java.util.List;
import java.util.Map;

public enum Family {
    ALL("ALL", "All", null),
    PRESS("PRESS", "Press", "/icons/press.svg"),
    CURL("CURL", "Curl", "/icons/curl.svg"),
    POMPES("POMPES", "Pompes", "/icons/pompes.svg"),
    ELEVATION("ELEVATION", "Élévation", "/icons/elevation-laterale.svg"),
    TIRAGE_ELASTIQUE("TIRAGE_ELASTIQUE", "Tirage élastique", "/icons/tirage-elastique.svg"),
    PLANCHE("PLANCHE", "Planche", "/icons/planche.svg"),
    SQUAT("SQUAT", "Squat", "/icons/squat.svg"),
    DEADLIFT("DEADLIFT", "Deadlift", "/icons/deadlift.svg");

    private final String code;
    private final String label;
    private final String iconPath;

    Family(String code, String label, String iconPath) {
        this.code = code;
        this.label = label;
        this.iconPath = iconPath;
    }

    public String getCode()     { return code; }
    public String getLabel()    { return label; }
    public String getIconPath() { return iconPath; }
    public boolean hasIcon()    { return iconPath != null; }

    public static Family fromCode(String code) {
        if (code == null || code.isBlank()) return null;
        for (Family f : values()) {
            if (f.code.equalsIgnoreCase(code)) return f;
        }
        throw new IllegalArgumentException("Unknown Family code: " + code);
    }
}