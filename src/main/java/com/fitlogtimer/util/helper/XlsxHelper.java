package com.fitlogtimer.util.helper;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
public class XlsxHelper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private XlsxHelper(){}

    public static double parseDouble(String valeur) {
        if (valeur == null || valeur.isEmpty()) {
            return 0.0;
        }

        valeur = valeur.trim().replace(",", ".");

        if (valeur.equalsIgnoreCase("NA") || valeur.equalsIgnoreCase("N/A") || valeur.equalsIgnoreCase("--")) {
            return 0.0;
        }

        try {
            return Double.parseDouble(valeur);
        } catch (NumberFormatException e) {
            log.error("Erreur de conversion en double pour la valeur '{}'", valeur);
            return 0.0;
        }
    }

    public static int parseReps(String valeur) {
        if (valeur == null || valeur.isEmpty()) {
            return -1;
        }

        try {
            // Ne garder que chiffres et éventuellement un point décimal
            valeur = valeur.replaceAll("[^\\d.]", "");

            if (valeur.contains(".")) {
                return (int) Double.parseDouble(valeur);
            }

            return Integer.parseInt(valeur);
        } catch (NumberFormatException e) {
            log.error("Erreur de conversion en nombre de répétitions pour la valeur '{}'", valeur);
            return -1;
        }
    }

    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty() || dateStr.trim().equalsIgnoreCase("NA")) {
            throw new IllegalArgumentException("La date ne peut pas être nulle ou vide");
        }
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format de date invalide : " + dateStr, e);
        }
    }
}
