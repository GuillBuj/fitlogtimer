package com.fitlogtimer.constants;

import java.util.Set;

public final class FileConstants {
    private FileConstants() {} // Empêche l'instanciation

    // Chemin relatif depuis la racine du projet
    public static final String EXCEL_DIR = "docs/";
    public static final String EXCEL_FILE_NAME = "DCevoModParse.xlsx";
    public static final String EXCEL_FILE = EXCEL_DIR + EXCEL_FILE_NAME;
    
    // Noms des feuilles
    public static final String HEAVY_WORKOUT_SHEET = "DC séance lourde";
    public static final String LIGHT_WORKOUT_SHEET = "DC séance légère";
    public static final String VAR_WORKOUT_SHEET = "DC variantes";
    public static final String DEADLIFT_SHEET = "Max Deadlift";
    public static final String GENERIC_WORKOUT_SHEET = "Generic";

    public static final Set<String> GENERIC_END_MARKERS = Set.of("---", "FIN", "END");
}