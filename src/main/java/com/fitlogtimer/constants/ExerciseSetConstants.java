package com.fitlogtimer.constants;

import java.util.Set;

public final class ExerciseSetConstants {
    private ExerciseSetConstants() {}

    public static final class SetTypes {
        public static final String MAX = "MAX";
        public static final String BODYWEIGHT_PAUSED = "BODYWEIGHT_PAUSED";
        public static final String HEAVY = "HEAVY";
        public static final String MEDIUM_55 = "MEDIUM/55";
        public static final String LIGHT_50 = "LIGHT/50";

        public static final Set<String> ALL = Set.of(
            MAX, BODYWEIGHT_PAUSED, HEAVY, MEDIUM_55, LIGHT_50
        );
    }
}
