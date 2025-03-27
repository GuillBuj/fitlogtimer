package com.fitlogtimer.parser;


import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.Workout;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SimpleWorkoutParser {

    public List<Workout> parseDeadliftWorkouts(String filePath) throws Exception {
        List<Workout> workouts = new ArrayList<>();
        final String FILE_NAME = "/docs/DCEvo.ods";
        final String SHEET_NAME = "Max Deadlift"; // Nom de la feuille cible

        try (InputStream is = Files.newInputStream(Paths.get(FILE_NAME));
             Workbook workbook = WorkbookFactory.create(is)) {
            
            // Récupération de la feuille par nom
            Sheet sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null) {
                throw new IllegalStateException("Feuille '" + SHEET_NAME + "' introuvable");
            }

            DataFormatter dataFormatter = new DataFormatter();

            // Configuration des indexes de ligne selon votre structure
            Row dateRow = sheet.getRow(0);       // Ligne des dates
            Row weightRow = sheet.getRow(1);      // Ligne des poids
            Row bodyWeightRow = sheet.getRow(5);   // Ligne du poids corporel

            // Parcours des colonnes (colonne B à la fin)
            for (int col = 1; col < dateRow.getLastCellNum(); col++) {
                try {
                    if (dateRow == null || weightRow == null || bodyWeightRow == null) {
                        throw new IllegalStateException("Une des lignes de données est introuvable dans la feuille.");
                    }
                    Workout workout = createWorkoutFromCells(
                        
                        dateRow.getCell(col),
                        bodyWeightRow.getCell(col),
                        weightRow.getCell(col),
                        dataFormatter
                    );
                    workouts.add(workout);
                } catch (Exception e) {
                    log.error("Erreur lors du traitement de la colonne {}: {}", col, e.getMessage(), e);
                }
            }
        }
        return workouts;
    }

    // Helper methods
    private Workout createWorkoutFromCells(Cell dateCell, Cell bodyWeightCell, 
                                         Cell weightCell, DataFormatter formatter) {
        Workout workout = new Workout();
        
        // Traitement de la date
        String dateStr = formatter.formatCellValue(dateCell);
        workout.setDate(LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("MM/dd/yy")));

        // Poids corporel
        if (bodyWeightCell != null) {
            workout.setBodyWeight(getCellNumericValue(bodyWeightCell, formatter));
        }

        // Création de l'ExerciseSet pour le deadlift
        ExerciseSet set = new ExerciseSet();
        set.setExercise(getOrCreateExercise("Deadlift"));
        set.setWeight(getCellNumericValue(weightCell, formatter));
        set.setRepNumber(1);
        set.setMax(true);
        set.setWorkout(workout);

        workout.setSetRecords(List.of(set));
        return workout;
    }

    private double getCellNumericValue(Cell cell, DataFormatter formatter) {
        if (cell == null) return 0.0;
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(formatter.formatCellValue(cell));
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            default:
                return 0.0;
        }
    }

    private Exercise getOrCreateExercise(String name) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
    
        return exercise;
    }
}