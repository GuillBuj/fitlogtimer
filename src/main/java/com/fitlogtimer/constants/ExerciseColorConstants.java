package com.fitlogtimer.constants;

import java.util.Map;

public final class ExerciseColorConstants {
    
    private ExerciseColorConstants() {}

    public static final Map<String, String> COLORS = Map.of(
        "DC", "#3E4A59",  // Gris très foncé
        "DC30", "#5C6A77",  // Gris-vert plus clair
        "DCS", "#4B5E4C",  // Vert foncé légèrement plus marqué
        "DM", "#5A4A66",  // Violet foncé (ajusté pour plus de contraste)
        "DC15", "#5A6B7C",  // Gris-bleu clair, similaire à DC30
        "DCD", "#7A8C9B",  // Gris clair mais un peu plus chaud
        "DL", "#A53A3A"  // Rouge sombre (Firebrick)
    );

    public static String getColorForExercise(String exerciseName) {
        return COLORS.getOrDefault(exerciseName, "#CCCCCC"); // Couleur par défaut
    }
}

// // Gris et dégradés
// "DC", "#3E4A59",  // Gris très foncé
// "DC2", "#4C5A66",  // Gris clair
// "DC3", "#2D3846",  // Gris encore plus foncé
// "DC30", "#5C6A77",  // Gris-vert plus clair
// "DC30-2", "#7A8C97",  // Gris-vert clair
// "DC30-3", "#4A5967",  // Gris-vert plus marqué
// "DC15", "#5A6B7C",  // Gris-bleu clair
// "DC15-2", "#6A7B8C",  // Gris-bleu plus clair
// "DC15-3", "#4E6476",  // Gris-bleu plus foncé

// // Vert foncé
// "DCS", "#4B5E4C",  // Vert foncé
// "DCS2", "#3A4D3C",  // Vert foncé naturel
// "DCS3", "#5C6D55",  // Vert plus doux
// "DCS4", "#3E523F",  // Vert très foncé

// // Violet et bleu foncé
// "DM", "#4A3E57",  // Violet foncé
// "DM2", "#3E2F48",  // Violet plus foncé
// "DM3", "#5A4A66",  // Violet plus clair
// "DM4", "#3F2B3F",  // Violet sombre
// "DMD", "#2A3446",  // Bleu très foncé

// // Gris clair
// "DCD", "#7A8C9B",  // Gris clair
// "DCD2", "#8D9A9F",  // Gris clair plus chaud
// "DCD3", "#B0BCC0",  // Gris clair doux
// "DCD4", "#A0B0B8",  // Gris clair bleuâtre

// // Rouge foncé (DL)
// "DL", "#B22222",  // Rouge foncé
// "DL2", "#A53A3A",  // Rouge plus foncé
// "DL3", "#9C1F1F",  // Rouge sombre
// "DL4", "#8B0000",  // Rouge très foncé

// // Jaune
// "Yellow", "#FFD93D",  // Jaune vif
// "Yellow2", "#F2C94C",  // Jaune doré
// "Yellow3", "#E1B40B",  // Jaune moutarde
// "Yellow4", "#C99D00",  // Jaune foncé

// // Marron
// "Brown", "#8B5C42",  // Marron moyen
// "Brown2", "#6A4E39",  // Marron foncé
// "Brown3", "#4B3621",  // Marron chocolat
// "Brown4", "#3E2B1C"   // Marron profond
