package com.fitlogtimer.enums;

public enum Trend {
    UP("trend-up"),
    SLIGHTLY_UP("trend-slightly-up"),
    NEUTRAL("trend-neutral"),
    SLIGHTLY_DOWN("trend-slightly-down"),
    DOWN("trend-down");



    private final String cssClass;

    Trend(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getCssClass() {
        return cssClass;
    }
}
