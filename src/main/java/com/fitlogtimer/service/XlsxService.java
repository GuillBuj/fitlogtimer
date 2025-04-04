package com.fitlogtimer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fitlogtimer.constants.FileConstants;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDCHeavyDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDCLightDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDeadliftDTO;
import com.fitlogtimer.mapper.XlsxMapper;
import com.fitlogtimer.parser.XlsxReader;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class XlsxService {

    private final XlsxMapper xlsxMapper;
    private final XlsxReader xlsxReader;

    public XlsxService(){
        this.xlsxMapper = new XlsxMapper();
        this.xlsxReader = new XlsxReader();
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
            log.info("{} DTOs", workouts.size());
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

            xlsxReader.printFormattedData(data);
            String[][] transposedData = xlsxReader.transposeArray(data);

            for (String[] column : transposedData) {
                if (!column[0].equals("NA")) {
                   workouts.add(xlsxMapper.mapToFromXlsxDCLightDTO(column));  
                }
            }
            //System.out.println("***1*** " + xlsxMapper.mapToFromXlsxDCHeavyDTO(transposedData[1]));
            workouts.forEach(workout -> log.info("Workout: {}", workout));
            // log.info("{} DTOs", workouts.size());
            // log.info("DTOs: {}", workouts);
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

            workouts.forEach(workout -> log.info("Workout: {}", workout));
            //log.info("{} DTOs", workouts.size());
            //log.info("DTOs: {}", workouts);
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier Excel: " + e.getMessage());
        }
        
        return workouts;
    }

    public static void main(String[] args){
        XlsxService xlsxService= new XlsxService();
        xlsxService.extractDTOsLightSheet();
    }
}
