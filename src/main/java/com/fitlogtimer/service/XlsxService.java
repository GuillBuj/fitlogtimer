package com.fitlogtimer.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.fitlogtimer.FitlogtimerApplication;
import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.dto.fromxlsx.*;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.util.helper.XlsxHelper;
import com.fitlogtimer.util.parser.GenericStrengthWorkoutParser;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import com.fitlogtimer.constants.FileConstants;
import com.fitlogtimer.mapper.XlsxMapper;
import com.fitlogtimer.parser.XlsxReader;

import lombok.extern.slf4j.Slf4j;

@Service
//@AllArgsConstructor
@Slf4j
public class XlsxService {

    private final XlsxMapper xlsxMapper;
    private final XlsxReader xlsxReader;
    //private final GenericStrengthWorkoutParser genericStrengthWorkoutParser;

    //private final ExerciseRepository exerciseRepository;

    public XlsxService(ExerciseRepository exerciseRepository){
        this.xlsxMapper = new XlsxMapper(exerciseRepository);
        this.xlsxReader = new XlsxReader();
        //this.genericStrengthWorkoutParser = new GenericStrengthWorkoutParser(exerciseRepository);
       // this.exerciseRepository = exerciseRepository;
    }



    public List<FromXlsxDCHeavyDTO> extractDTOsHeavySheetRegular(){
        String excelFilePath = FileConstants.EXCEL_FILE;
        String sheetName = FileConstants.HEAVY_WORKOUT_SHEET;
        int startRow = 0;
        int startColumn = 1;
        int endRow = 14;
        int endColumn = 43;

        List<FromXlsxDCHeavyDTO> workouts = new ArrayList<>();
        try {
            String[][] data = xlsxReader.readSheetData(excelFilePath, sheetName, startRow, startColumn, endRow, endColumn);

            //xlsxReader.printFormattedData(data);
            String[][] transposedData = xlsxReader.transposeArray(data);

            for (String[] column : transposedData) {
                if (column[0].equals("NA")) {
                    workouts.add(xlsxMapper.mapToFromXlsxDCHeavyDTO(column));  
                }
            }
            //System.out.println("***1*** " + xlsxMapper.mapToFromXlsxDCHeavyDTO(transposedData[1]));
            //workouts.forEach(workout -> log.info("Workout: {}", workout));
            //log.info("{} DTOs", workouts.size());
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier Excel: " + e.getMessage());
        }
        
        return workouts;
    }

    public List<FromXlsxDCLightDTO> extractDTOsLightSheet(){
        String excelFilePath = FileConstants.EXCEL_FILE;
        String sheetName = FileConstants.LIGHT_WORKOUT_SHEET;
        int startRow = 1;
        int startColumn = 2;
        int endRow = 12;
        int endColumn = 151;

        List<FromXlsxDCLightDTO> workouts = new ArrayList<>();
        try {
            String[][] data = xlsxReader.readSheetData(excelFilePath, sheetName, startRow, startColumn, endRow, endColumn);

            //xlsxReader.printFormattedData(data);
            String[][] transposedData = xlsxReader.transposeArray(data);

            for (String[] column : transposedData) {
                if (!column[0].equals("NA")) {
                   workouts.add(xlsxMapper.mapToFromXlsxDCLightDTO(column));  
                }
            }
            //System.out.println("***1*** " + xlsxMapper.mapToFromXlsxDCHeavyDTO(transposedData[1]));
            //workouts.forEach(workout -> log.info("Workout: {}", workout));
            // log.info("{} DTOs", workouts.size());
            // log.info("DTOs: {}", workouts);
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier Excel: " + e.getMessage());
        }
        
        return workouts;
    }

    public List<FromXlsxDCVarDTO> extractDTOsVarSheet(){
        String excelFilePath = FileConstants.EXCEL_FILE;
        String sheetName = FileConstants.VAR_WORKOUT_SHEET;
        int startRow = 1;
        int startColumn = 3;
        int endRow = 14;
        int endColumn = 29;

        List<FromXlsxDCVarDTO> workouts = new ArrayList<>();
        try {
            String[][] data = xlsxReader.readSheetData(excelFilePath, sheetName, startRow, startColumn, endRow, endColumn);

            //xlsxReader.printFormattedData(data);
            String[][] transposedData = xlsxReader.transposeArray(data);

            for (String[] column : transposedData) {
                if (!column[0].equals("NA")) {
                   workouts.add(xlsxMapper.mapToFromXlsxDCVarDTO(column));  
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier Excel: " + e.getMessage());
        }
        
        return workouts;
    }


    public List<FromXlsxDeadliftDTO> extractDTOsDeadliftSheet(){
        String excelFilePath = FileConstants.EXCEL_FILE;
        String sheetName = FileConstants.DEADLIFT_SHEET;
        int startRow = 3;
        int startColumn = 1;
        int endRow = 5;
        int endColumn = 20;

        List<FromXlsxDeadliftDTO> workouts = new ArrayList<>();
        try {
            String[][] data = xlsxReader.readSheetData(excelFilePath, sheetName, startRow, startColumn, endRow, endColumn);

            //xlsxReader.printFormattedData(data);
            String[][] transposedData = xlsxReader.transposeArray(data);
            
            for (String[] column : transposedData) {
                workouts.add(xlsxMapper.mapToFromXlsxDeadliftDTO(column));  
            }

            //workouts.forEach(workout -> log.info("Workout: {}", workout));
            //log.info("{} DTOs", workouts.size());
            //log.info("DTOs: {}", workouts);
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier Excel: " + e.getMessage());
        }
        
        return workouts;
    }

    public FromXlsxGenericDTO extractGenericSheet(String sheetName) throws IOException {

        String[][] rawData = xlsxReader.readSheetData(FileConstants.EXCEL_FILE, sheetName, 0, 1);
        log.info("rawData: {}", (Object) rawData);

        int endRow = xlsxReader.findEndRow(rawData);
        int endCol = xlsxReader.findEndColumn(rawData);
        log.info("endRow: {}, endCol: {}", endRow, endCol);

        // On tronque au bon nombre de lignes/colonnes
        String[][] trimmed = xlsxReader.trim(rawData, endRow, endCol);
        log.info("trimmed: {}",(Object) trimmed);

        String[][] data = xlsxReader.transposeArray(trimmed); // chaque colonne représente un workout
        log.info("data: {}", (Object) data);

        // B1 = nom de la séance
        String name = rawData[0][0];
        log.info("name: {}", name);

        List<String> shortNameList = new ArrayList<>();
        List<String> barWeightList = new ArrayList<>();

        for (int i = 2; i < endRow; i++) {
            String shortName = rawData[i][0];

            // Stop si on atteint un marqueur de fin
            if (FileConstants.GENERIC_END_MARKERS.contains(shortName)) {
                break;
            }

            shortNameList.add(shortName);
            barWeightList.add(rawData[i][1]);
        }

        String[] shortNameColumn = shortNameList.toArray(new String[0]);
        String[] barWeightColumn = barWeightList.toArray(new String[0]);

        log.info("shortNameColumn(size:{}): {}", shortNameColumn.length, (Object) shortNameColumn);
        log.info("barWeightColumn(size:{}): {}", barWeightColumn.length, (Object) barWeightColumn);

        List<FromXlsxGenericWorkoutDTO> workouts = new ArrayList<>();

        for (int col = 2; col < data.length; col++) {
            //if (col == 4 || col == 21) { // TODO: à supprimer une fois que ça fonctionne
                log.info("*** Next workout *** col {}", col);
                String[] dataColumn = Arrays.copyOf(data[col], endRow);
                log.info("dataColumn(size: {}): {}", dataColumn.length, dataColumn);
                FromXlsxGenericWorkoutDTO workout = xlsxMapper.mapToFromXlsxGenericWorkoutDTO(
                        dataColumn,
                        shortNameColumn,
                        barWeightColumn,
                        col
                );
                log.info("workout post mapper: {}", (Object) workout);
                if (workout.sets() != null && !workout.sets().isEmpty()) {
                    workouts.add(workout);
                }
                log.info("workouts: {}", (Object) workouts);
            }
        //}

        log.info("name: {}, workouts size: {}, workouts: {}", name, workouts.size(), workouts);
        return new FromXlsxGenericDTO(name, workouts);
    }

    public static boolean isEndMarker(String value) {
        return value != null && FileConstants.GENERIC_END_MARKERS.contains(value.trim());
    }


//    public static void main(String[] args) throws IOException {
//        ApplicationContext context = SpringApplication.run(FitlogtimerApplication.class, args);
//        ExerciseRepository exerciseRepository = context.getBean(ExerciseRepository.class);
//
//        XlsxService xlsxService= new XlsxService(exerciseRepository);
//        xlsxService.extractGenericSheet("Muscu46 comp");
//    }
}
