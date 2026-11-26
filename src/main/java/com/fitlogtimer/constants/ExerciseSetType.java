package com.fitlogtimer.constants;

import java.util.Map;

public final class ExerciseSetType {

    private ExerciseSetType() {
    }

    public static final String FREE_WEIGHT = "FREE_WEIGHT";
    public static final String BODYWEIGHT = "BODYWEIGHT";
    public static final String ISOMETRIC = "ISOMETRIC";
    public static final String ELASTIC = "ELASTIC";
    public static final String MOVEMENT = "MOVEMENT";

    public static final Map<String, String> DISPLAY_NAMES = Map.of(
        FREE_WEIGHT, "Poids libres",
        BODYWEIGHT, "Poids de corps",
        ISOMETRIC, "Isométrique",
        ELASTIC, "Bandes élastiques",
        MOVEMENT, "Renfo mouvement"
    );

    public static final Map<String, String> ICON_PATHS = Map.of(
            FREE_WEIGHT, "/icons/free-weight-1.svg",
            BODYWEIGHT, "/icons/bodyweight.svg",
            ISOMETRIC, "/icons/isometric.svg",
            ELASTIC, "/icons/elastic.svg",
            MOVEMENT, "/icons/movement.svg"
    );
}
