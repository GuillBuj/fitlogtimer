package com.fitlogtimer.util.parser;

import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxGenericWorkoutDTO;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.util.helper.XlsxHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class GenericStrengthWorkoutParser {
    private static final String END_MARKER = "---";

    private final ExerciseRepository exerciseRepository;


//    public FromXlsxGenericWorkoutDTO parseColumn(String[][] data, int columnIndex, int workoutId) {
//        LocalDate date = data[0][columnIndex];
//        double bodyWeight = XlsxHelper.parseDouble(data[1][columnIndex]);
//
//        List<ExerciseSetCreateDTO> sets = new ArrayList<>();
//        Exercise currentExercise = null;
//        double barWeight = 0.0;
//
//        for (int row = 2; row < data.length; row++) {
//            String columnShortName = data[row][1]; // colonne B = shortName
//
//            if (END_MARKER.equalsIgnoreCase(columnShortName)) {
//                break;
//            }
//
//            String cell = data[row][columnIndex];
//
//            if (columnShortName != null && !columnShortName.isBlank()) {
//                String shortName = columnShortName.trim();
//                currentExercise = exerciseRepository.findByShortName(shortName);
//                barWeight = XlsxHelper.parseDouble(data[row][2]);
//            } else if (currentExercise != null && cell != null && !cell.isBlank()) {
//                double weight = currentExercise.getType().equalsIgnoreCase("ELASTIC")
//                        ? 0.0
//                        : XlsxHelper.parseDouble(cell) + barWeight;
//                String bands = currentExercise.getType().equalsIgnoreCase("ELASTIC")
//                        ? cell
//                        : "";
//                int nbReps = findNbRepsBelowIfPresent(data, row, columnIndex);
//
//                ExerciseSetCreateDTO set = new ExerciseSetCreateDTO(
//                        currentExercise.getId(),
//                        weight,
//                        nbReps,
//                        bands,
//                        0,
//                        "",
//                        "",
//                        "",
//                        workoutId,
//                        currentExercise.getType()
//                );
//
//                sets.add(set);
//            }
//        }
//
//        return new FromXlsxGenericWorkoutDTO(date, bodyWeight, sets);
//    }
//
//    private int findNbRepsBelowIfPresent(String[][] data, int currentRow, int columnIndex){
//        for(int i = 1; i<=2; i++){
//            if (currentRow + i < data.length){
//                int reps = XlsxHelper.parseReps(data[currentRow+i][columnIndex]);
//                if (reps > 0){
//                    return reps;
//                }
//            }
//        }
//        return 0;
//    }

}
