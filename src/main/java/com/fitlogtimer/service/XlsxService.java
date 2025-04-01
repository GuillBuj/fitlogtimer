package com.fitlogtimer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fitlogtimer.constants.FileConstants;
import com.fitlogtimer.dto.FromXlsxDCHeavyDTO;
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

    public List<FromXlsxDCHeavyDTO> extractDTOs(){
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
            

            //xlsxReader.printSpecificData(transposedData, 1);
            for(int i= 0; i<transposedData.length-1; i++){
                if(transposedData[i][0].equals("NA"))
                workouts.add(xlsxMapper.mapToFromXlsxDCHeavyDTO(transposedData[i]));  
            }
            //System.out.println("***1*** " + xlsxMapper.mapToFromXlsxDCHeavyDTO(transposedData[1]));
            //workouts.forEach(workout -> log.info("Workout: {}", workout));
            log.info("{} DTOs", workouts.size());
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier Excel: " + e.getMessage());
        }
        
        return workouts;
    }

    public static void main(String[] args){
        XlsxService xlsxService= new XlsxService();
        xlsxService.extractDTOs();
    }
}
