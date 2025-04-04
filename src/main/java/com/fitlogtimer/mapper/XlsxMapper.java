package com.fitlogtimer.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fitlogtimer.dto.base.SetBasicDTO;
import com.fitlogtimer.dto.base.SetBasicWithExDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDCHeavyDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDCLightDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDeadliftDTO;

@Component
public class XlsxMapper {

    public FromXlsxDCHeavyDTO mapToFromXlsxDCHeavyDTO(String[] column) {

        LocalDate date = LocalDate.parse(column[1], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        double bodyWeight = parseDouble(column[14]);
        String type = column[0];

        List<SetBasicDTO> sets = new ArrayList<>();
        for (int i = 2; i < column.length-1; i += 2) {
            double weight = parseDouble(column[i]);
            int reps = parseReps(column[i + 1]);

            if (reps >= 0) {
                sets.add(new SetBasicDTO(reps, weight + 8)); //barre
            }
        }

        return new FromXlsxDCHeavyDTO(date, bodyWeight, sets, type);
    }

    public FromXlsxDCLightDTO mapToFromXlsxDCLightDTO(String[] column) {

        LocalDate date = LocalDate.parse(column[1], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        double bodyWeight = parseDouble(column[2]);

        String type = column[0];
        String ex1="", ex2="";
        switch(type){
            case "DC DC15": {ex1="DC"; ex2="DC15"; break;}
            case "DC15 DC": {ex1="DC15"; ex2="DC"; break;}
            case "DC15 DCD": {ex1="DC15"; ex2="DCD"; break;}
            case "DC DCD": {ex1="DC"; ex2="DCD"; break;}
            case "4DC15 DCD": {ex1="DC15"; ex2="DCD"; break;}
            case "DC": {ex1="DC"; break;}
        }


        List<SetBasicWithExDTO> sets = new ArrayList<>();
        sets.add(new SetBasicWithExDTO(parseReps(column[4]), parseDouble(column[3])+8, ex1));
        sets.add(new SetBasicWithExDTO(parseReps(column[5]), parseDouble(column[3])+8, ex1));
        sets.add(new SetBasicWithExDTO(parseReps(column[6]), parseDouble(column[3])+8, ex1));
        if(!type.equals("4DC15 DCD") && !type.equals("DC")){
            sets.add(new SetBasicWithExDTO(parseReps(column[8]), parseDouble(column[7])+8, ex2));
            sets.add(new SetBasicWithExDTO(parseReps(column[9]), parseDouble(column[7])+8, ex2));
            sets.add(new SetBasicWithExDTO(parseReps(column[10]), parseDouble(column[7])+8, ex2));
        } else if(type.equals("4DC15 DCD")){
            sets.add(new SetBasicWithExDTO(parseReps(column[7]), parseDouble(column[3])+8, ex1));
            sets.add(new SetBasicWithExDTO(parseReps(column[8]), parseDouble(column[3])+8, ex2));
            sets.add(new SetBasicWithExDTO(parseReps(column[9]), parseDouble(column[3])+8, ex2));
            sets.add(new SetBasicWithExDTO(parseReps(column[10]), parseDouble(column[3])+8, ex2));
            sets.add(new SetBasicWithExDTO(parseReps(column[11]), parseDouble(column[3])+8, ex2));
        } else if(type.equals("DC")){
            if(!column[7].equals("NA")){sets.add(new SetBasicWithExDTO(parseReps(column[7]), parseDouble(column[3])+8, ex1));}
                else if(!column[8].equals("NA")){sets.add(new SetBasicWithExDTO(parseReps(column[8]), parseDouble(column[3])+8, ex1));}
        }

        return new FromXlsxDCLightDTO(date, bodyWeight, sets, type);
    }


    public FromXlsxDeadliftDTO mapToFromXlsxDeadliftDTO(String[] column) {

        LocalDate date = LocalDate.parse(column[0], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        double bodyWeight = parseDouble(column[2]);
        SetBasicDTO set = new SetBasicDTO(1, parseDouble(column[1]));

        return new FromXlsxDeadliftDTO(date, bodyWeight, set);
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
