package com.fitlogtimer.util.helper;

import java.time.LocalDate;

public class XlsxHelper {
    
    private XlsxHelper(){}

    public static double parseDouble(String value) {
        if (value == null || value.isEmpty()) {
            return 0.0;
        }

        value = value.replace(",", ".");

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.err.println("Erreur de conversion vers double: " + value);
            return 0.0;
        }
    }

    public static int parseReps(String value) {
        if (value == null || value.isEmpty()) {
            return -1;
        }

        try {
            value = value.replaceAll("[^\\d.]", "");

            if (value.contains(".")) {
                return (int) Double.parseDouble(value);
            }

            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("Erreur de conversion de reps : " + value);
            return -1;
        }
    }
}
