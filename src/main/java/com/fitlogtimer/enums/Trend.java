package com.fitlogtimer.enums;

public enum Trend {
    UP("trend-up"),
    DOWN("trend-down"),
    NEUTRAL("trend-neutral");

    private final String cssClass;

    Trend(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getCssClass() {
        return cssClass;
    }
}
