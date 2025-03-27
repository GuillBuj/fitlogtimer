// package com.fitlogtimer.parser;

// import org.odftoolkit.simple.SpreadsheetDocument;
// import org.odftoolkit.simple.table.Table;
// import org.odftoolkit.simple.table.Row;

// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
// import java.util.List;

// import com.fitlogtimer.model.Exercise;
// import com.fitlogtimer.model.ExerciseSet;
// import com.fitlogtimer.model.Workout;

// public class SimpleWorkoutParser {
//     public List<Workout> parseSingleRepWorkouts(String filePath) throws Exception {
//         SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(filePath);
//         Table sheet = doc.getTableByName("Feuille1"); // Nom réel de votre feuille
//         List<Workout> workouts = new ArrayList()<>();

//         // Ligne 1 : Dates (header)
//         Row dateRow = sheet.getRowByIndex(0);
//         // Ligne 2 : Poids 1RM (index 1 = ligne "1rep")
//         Row weightRow = sheet.getRowByIndex(1);
//         // Ligne 6 : Poids du corps (index 5 = ligne "Poids / Lvl")
//         Row bodyWeightRow = sheet.getRowByIndex(5);

//         for (int col = 1; col < dateRow.getCellCount(); col++) {
//             // 1. Création de la Workout
//             Workout workout = new Workout();
//             workout.setDate(LocalDate.parse(dateRow.getCellByIndex(col).getStringValue(), 
//                           DateTimeFormatter.ofPattern("MM/dd/yy")));
//             workout.setBodyWeight(Double.parseDouble(bodyWeightRow.getCellByIndex(col).getDisplayText()));

//             // 2. Création de l'ExerciseSet (1RM)
//             ExerciseSet set = new ExerciseSet();
//             set.setExercise(getOrCreateExercise("1RM")); // Méthode à implémenter
//             set.setWeight(Double.parseDouble(weightRow.getCellByIndex(col).getDisplayText()));
//             set.setRepNumber(1);
//             set.setMax(true);
//             set.setWorkout(workout);

//             workout.setSetRecords(List.of(set));
//             workouts.add(workout);
//         }

//         doc.close();
//         return workouts;
//     }

//     private Exercise getOrCreateExercise(String name) {
//         Exercise exercise = new Exercise();
//         exercise.setName(name);
//         // Alternative: exerciseRepository.findByName(name) si en base
//         return exercise;
//     }
// }