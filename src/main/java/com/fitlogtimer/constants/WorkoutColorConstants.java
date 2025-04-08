package com.fitlogtimer.constants;

import java.util.Map;

public class WorkoutColorConstants {
    
    private WorkoutColorConstants() {}

    public static final Map<String, String> COLORS = Map.of(
        "HEAVY", "#D32F2F",  // Rouge intense pour les séances lourdes
        "LIGHT", "#4CAF50",   // Vert pour les séances légères
        "VAR", "#FF9800",     // Orange pour les séances variables
        "DL", "#3F51B5"      // Bleu pour les Deadlifts
        // "CARDIO", "#FFC107",  // Jaune pour les séances de cardio
        // "MOBILITY", "#9C27B0" // Violet pour les séances de mobilité
    );

    public static String getColorForWorkoutType(String workoutType) {
        return COLORS.getOrDefault(workoutType, "#CCCCCC"); // Couleur par défaut
    }
}
