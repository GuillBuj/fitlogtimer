package com.fitlogtimer.constants;

import java.util.Map;

public final class ExerciseColorConstants {
    
    private ExerciseColorConstants() {}

    public static final Map<String, String> COLORS = Map.ofEntries(
            Map.entry("DC", "#3E4A59"),
            Map.entry("DC30", "#5C6A77"),
            Map.entry("DCS", "#4B5E4C"),
            Map.entry("DM", "#5A4A66"),
            Map.entry("D15", "#5A6B7C"),
            Map.entry("DCD", "#7A8C9B"),
            Map.entry("DL", "#A53A3A"),
            Map.entry("B", "#000000"),
            Map.entry("HSQ", "#1A237E"),
            Map.entry("SH", "#2B2B2B"),
            Map.entry("DE", "#3D0C3C"),
            Map.entry("DO", "#381819"),
            Map.entry("TH", "#3B3C36"),
            Map.entry("TVH", "#2C3539")

    );

//    Noir	#000000
//    Gris charbon	#2B2B2B
//    Bleu nuit	#0B3D91
//    Bordeaux	#581845
//    Vert sapin	#014421
//    Bleu pétrole	#003B46
//    Gris anthracite	#3B3B3B
//    Marron foncé	#4B2E2E
//    Indigo foncé	#1A237E
//    Prune	#3D0C3C
//    Vert forêt	#0B3D0B
//    Bleu marine	#000080
//    Gris plomb	#1E1E1E
//    Rouge foncé	#8B0000
//    Brun chocolat	#381819
//    Violet foncé	#301934
//    Acier bleuté	#2C3539
//    Olive foncée	#3B3C36
//    Bleu minuit	#191970
//    Ardoise foncée	#2F4F4F


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
