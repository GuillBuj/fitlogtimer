package com.fitlogtimer.constants;

import java.util.Set;

public final class WorkoutConstants {
    private WorkoutConstants() {}

    public static final class WorkoutTypes {
        public static final String MAX = "MAX";
        public static final String HEAVY = "HEAVY";
        public static final String MEDIUM = "MEDIUM";
        public static final String LIGHT = "LIGHT";

        public static final Set<String> ALL = Set.of(
            MAX, HEAVY, MEDIUM, LIGHT
        );
    }
}
