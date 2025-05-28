package com.fitlogtimer.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fitlogtimer.constants.FileConstants;
import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.dto.fromxlsx.*;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.util.helper.XlsxHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fitlogtimer.constants.BarConstants;
import com.fitlogtimer.dto.base.SetBasicDTO;
import com.fitlogtimer.dto.base.SetBasicWithExDTO;

import static com.fitlogtimer.util.helper.XlsxHelper.*;

@Component
@AllArgsConstructor
@Slf4j
public class XlsxMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ExerciseRepository exerciseRepository;

    public FromXlsxDCHeavyDTO mapToFromXlsxDCHeavyDTO(String[] column) {

        LocalDate date = parseDate(column[1]);
        double bodyWeight = parseDouble(column[14]);
        String type = column[0];

        List<SetBasicDTO> sets = new ArrayList<>();
        for (int i = 2; i < column.length-1; i += 2) {
            double weight = parseDouble(column[i]);
            int reps = parseReps(column[i + 1]);

            if (reps >= 0) {
                sets.add(new SetBasicDTO(reps, weight + BarConstants.BarType.STRAIGHT.getWeight()));
            }
        }

        return new FromXlsxDCHeavyDTO(date, bodyWeight, sets, type);
    }

    public FromXlsxDCLightDTO mapToFromXlsxDCLightDTO(String[] column) {

        LocalDate date = parseDate(column[1]);
        double bodyWeight = parseDouble(column[2]);

        ExercisePair exercisePair = determineExercises(column[0]);
        List<SetBasicWithExDTO> sets = buildExerciseSets(column, exercisePair, BarConstants.BarType.EZ.getWeight());

        return new FromXlsxDCLightDTO(date, bodyWeight, sets, column[0]);
    }

    public FromXlsxDCVarDTO mapToFromXlsxDCVarDTO(String[] column) {

        LocalDate date = parseDate(column[0]);
        double bodyWeight = parseDouble(column[1]);

        List<SetBasicWithExDTO> sets = new ArrayList<>();
        double weightOffset = BarConstants.BarType.STRAIGHT.getWeight();
        add3Sets(sets, column, 2, "DCS", weightOffset);
        add3Sets(sets, column, 6, "DM", weightOffset);
        add3Sets(sets, column, 10, "DC30", weightOffset);

        return new FromXlsxDCVarDTO(date, bodyWeight, sets);
    }

    public FromXlsxGenericWorkoutDTO mapToFromXlsxGenericWorkoutDTO(String[] dataColumn, String[] shortNameColumn, int workoutId) {
        LocalDate date = parseDate(dataColumn[0]);
        double bodyWeight = parseDouble(dataColumn[1]);

        List<ExerciseSetCreateDTO> sets = new ArrayList<>();
        Exercise currentExercise = null;
        double barWeight = 0.0;

        for (int row = 2; row < dataColumn.length; row++) {
            // Décalage ici : shortNameColumn index commence à 0 pour dataColumn[2]
            String shortName = shortNameColumn[row - 2];

            if (FileConstants.GENERIC_END_MARKERS.contains(shortName)) {
                break;
            }

            String cell = dataColumn[row];

            if(!cell.equals("NA")){
                log.info("cell: {} shortName: {} currentExercise: {} barWeight: {}", cell, shortName, currentExercise, barWeight);
            if (!shortName.equals("NA")) {
                currentExercise = exerciseRepository.findByShortName(shortName.trim().toUpperCase());
                barWeight = parseDouble(cell);
                log.info("short name : {} currentExercise: {} barWeight: {}", shortName, currentExercise, barWeight);
            } else {
                log.info("- else -");
                if (currentExercise != null && cell != null && !cell.isBlank()) {
                    log.info("- else if -");
                    double weight = currentExercise.getType().equalsIgnoreCase("ELASTIC")
                            ? 0.0
                            : parseDouble(cell) + barWeight;

                    String bands = currentExercise.getType().equalsIgnoreCase("ELASTIC") ? cell : "";
                    int nbReps = findNbRepsBelowIfPresent(dataColumn, shortNameColumn, row);
                    log.info("currentExercise: {} weight: {} bands: {} nbReps: {}", currentExercise, weight, bands, nbReps);
                    ExerciseSetCreateDTO set = new ExerciseSetCreateDTO(
                            currentExercise.getId(),
                            weight,
                            nbReps,
                            bands,
                            0,
                            "",
                            "",
                            "",
                            workoutId,
                            currentExercise.getType()
                    );
                    log.info("set: {}", set);
                    sets.add(set);
                }
            }
            } else log.info("cell NA -> RIEN");
        }



//        for (int row = 2; row < dataColumn.length; row++) {
//            int shortNameIndex = row - 2;
//            String shortName = shortNameIndex >= 0 && shortNameIndex < shortNameColumn.length
//                    ? shortNameColumn[shortNameIndex]
//                    : null;
//
//            if (shortName != null && !shortName.isBlank() && !"NA".equalsIgnoreCase(shortName.trim())) {
//                currentExercise = exerciseRepository.findByShortName(shortName.trim());
//                barWeight = parseDouble(dataColumn[row]);
//                log.info("short name : {} currentExercise: {} barWeight: {}", shortName, currentExercise, barWeight);
//            } else if (currentExercise != null && dataColumn[row] != null && !dataColumn[row].isBlank()) {
//                double weight = currentExercise.getType().equalsIgnoreCase("ELASTIC")
//                        ? 0.0
//                        : parseDouble(dataColumn[row]) + barWeight;
//
//                String bands = currentExercise.getType().equalsIgnoreCase("ELASTIC") ? dataColumn[row] : "";
//                int nbReps = findNbRepsBelowIfPresent(dataColumn, shortNameColumn, row);
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
//                log.info("set: {}", set);
//                sets.add(set);
//            } else {
//                // Si shortName est NA ou vide et currentExercise est null, on ne fait rien
//                log.debug("Ignoring row {} due to shortName='{}' and currentExercise=null", row, shortName);
//            }
//        }


        return new FromXlsxGenericWorkoutDTO(date, bodyWeight, sets);
    }

    private int findNbRepsBelowIfPresent(String[] dataColumn, String[] shortNameColumn, int currentRow) {
        for (int i = 1; i <= 2; i++) {
            int nextRow = currentRow + i;
            if (nextRow < dataColumn.length) {
                if (shortNameColumn[nextRow] == null || shortNameColumn[nextRow].isBlank()) {
                    int reps = parseReps(dataColumn[nextRow]);
                    if (reps > 0) return reps;
                }
            }
        }
        return 0;
    }

    private ExercisePair determineExercises(String type) {
        return switch (type) {
            case "DC DC15" -> new ExercisePair("DC", "DC15");
            case "DC15 DC" -> new ExercisePair("DC15", "DC");
            case "DC15 DCD" -> new ExercisePair("DC15", "DCD");
            case "DC DCD" -> new ExercisePair("DC", "DCD");
            case "4DC15 DCD" -> new ExercisePair("DC15", "DCD");
            case "DC" -> new ExercisePair("DC", null);
            default -> throw new IllegalArgumentException("Unknown exercise type: " + type);
        };
    }

    private List<SetBasicWithExDTO> buildExerciseSets(String[] column, ExercisePair exercises, double weightOffset) {
        
        List<SetBasicWithExDTO> sets = new ArrayList<>();
        double firstWeight = parseDouble(column[3]) + weightOffset;
        double secondWeight = parseDouble(column[7]) + weightOffset;
    
        // 3 premiers sets toujours organisés pareil
        addSets(sets, column, 4, 6, firstWeight, exercises.ex1());
    
        // puis selon le type de la colonne
        if (exercises.ex2() != null) {
            if ("4DC15 DCD".equals(column[0])) {
                addSets(sets, column, 7, 7, firstWeight, exercises.ex1());
                addSets(sets, column, 8, 11, firstWeight, exercises.ex2());
            } else if ("DC".equals(column[0])) {
                addOptionalSets(sets, column, firstWeight, exercises.ex1(), 7, 8);
            } else {
                addSets(sets, column, 8, 10, secondWeight, exercises.ex2());
            }
        }
    
        return sets;
    }
    
    private void addOptionalSets(List<SetBasicWithExDTO> sets, String[] column, 
                               double weight, String exercise, int... indices) {
        for (int index : indices) {
            if (!"NA".equals(column[index])) {
                sets.add(new SetBasicWithExDTO(parseReps(column[index]), weight, exercise));
            }
        }
    }

    //1ère ligne: poids sans barre, 2/3/4 reps
    private void add3Sets(List<SetBasicWithExDTO> sets, String[] column, int index, String exercise, double weightOffset){
        addSets(sets, column, index+1, index+3, parseDouble(column[index]) + weightOffset, exercise);
    }

    private void addSets(List<SetBasicWithExDTO> sets, String[] column, int startIdx, int endIdx, 
                    double weight, String exercise) {
        for (int i = startIdx; i <= endIdx; i++) {
            if(parseReps(column[i])>0)
                sets.add(new SetBasicWithExDTO(parseReps(column[i]), weight, exercise));
        }
    }

    private record ExercisePair(String ex1, String ex2) {};

    public FromXlsxDeadliftDTO mapToFromXlsxDeadliftDTO(String[] column) {
        LocalDate date = parseDate(column[0]);
        double bodyWeight = parseDouble(column[2]);
        SetBasicDTO set = new SetBasicDTO(1, parseDouble(column[1]));

        return new FromXlsxDeadliftDTO(date, bodyWeight, set);
    }
 
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty() || dateStr.trim().equalsIgnoreCase("NA")) {
            throw new IllegalArgumentException("Date cannot be null or empty");
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }
}
