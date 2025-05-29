package com.fitlogtimer.constants;

import java.util.Map;

public final class WorkoutColorConstants {
    
    private WorkoutColorConstants() {}

    public static final Map<String, String> COLORS = Map.of(
        "HEAVY", "#FFCDD2",  // Rouge pâle pour les séances lourdes
        "LIGHT", "#C8E6C9",  // Vert pâle pour les séances légères
        "VAR", "#FFE0B2",    // Orange pâle pour les séances variables
        "DL", "#C5CAE9",    // Bleu pâle pour les Deadlifts
        "Muscu6+","#C8E6C9", //séance proche du Light
        "TigerShark", "#EDBB99",
            "TEST", "111111"
    );

    public static String getColorForWorkoutType(String workoutType) {
        if (workoutType == null) return "#F5F5F5";
        return COLORS.getOrDefault(workoutType, "#F5F5F5");
    }
}


// "CARDIO", "#FFF9C4", // Jaune pâle pour le cardio
        // "MOBILITY", "#E1BEE7", // Violet pâle pour la mobilité
        // "ENDURANCE", "#B3E5FC", // Bleu clair pour l'endurance
        // "STRETCHING", "#F5F5F5", // Gris très clair pour les étirements
        // "RECOVERY", "#D7CCC8",    // Brun grisâtre pour la récupération
        // "CIRCUIT", "#F8BBD0"      // Rose pâle pour les circuits