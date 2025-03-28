package com.fitlogtimer.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fitlogtimer.dto.FromXlsxDCHeavyDTO;
import com.fitlogtimer.dto.SetBasicDTO;

@Component
public class XlsxMapper {

    public FromXlsxDCHeavyDTO mapToFromXlsxDCHeavyDTO(String[] column) {

        LocalDate date = LocalDate.parse(column[1], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        //LocalDate date = LocalDate.now();
        double bodyWeight = parseDouble(column[14]);

        List<SetBasicDTO> sets = new ArrayList<>();
        for (int i = 2; i < column.length-1; i += 2) {
            double weight = parseDouble(column[i]);
            int reps = parseReps(column[i + 1]);

            if (reps >= 0) {
                sets.add(new SetBasicDTO(reps, weight));
            }
        }

        String type = column[0];

        return new FromXlsxDCHeavyDTO(date, bodyWeight, sets, type);
    }

    private double parseDouble(String value) {
        if (value == null || value.isEmpty()) {
            return 0.0;
        }

        value = value.replace(",", ".");

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.err.println("Erreur de conversion : " + value);
            return 0.0;
        }
    }

    private int parseReps(String value) {
        if (value == null || value.isEmpty()) {
            return -1;  // Retourner -1 si la valeur est vide ou nulle
        }
    
        try {
            // Remplacer tout caractère non numérique (y compris les virgules) avant de convertir
            value = value.replaceAll("[^\\d.]", "");
            
            // Si le nombre a un point décimal, on le considère comme un nombre entier
            if (value.contains(".")) {
                // Si c'est un nombre à virgule flottante, on ne garde que la partie entière
                return (int) Double.parseDouble(value); // Prendre la partie entière uniquement
            }
    
            // Si c'est un nombre entier, directement retourner l'entier
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("Erreur de conversion de reps : " + value);
            return -1;  // Si la conversion échoue, retourner -1
        }
    }
}
