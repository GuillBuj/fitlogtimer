package com.fitlogtimer.service;

import com.fitlogtimer.dto.fromxlsx.FromXlsxGenericDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final XlsxService xlsxService;
    private final WorkoutService workoutService;

    @Transactional
    public String importSheet(String sheetName) {
        try {
            String tagImport = "import" + sheetName.replaceFirst("^\\.", "");
            workoutService.deleteByTagImport(tagImport);
            FromXlsxGenericDTO fromXlsxGenericDTO= xlsxService.extractGenericSheet(sheetName);
            workoutService.createWorkoutsFromXlsxGenericDTO(fromXlsxGenericDTO);
            return "Import de '" + sheetName + "' réussi.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de l'import '" + sheetName + "' : " + e.getMessage();
        }
    }

    @Transactional
    public String importPartialSheet(String sheetName) {
        try {
            String tagImport = "import" + sheetName.replaceFirst("^\\.", "");

            FromXlsxGenericDTO fromXlsxGenericDTO= xlsxService.extractFilteredGenericSheet(sheetName);
            workoutService.createWorkoutsFromXlsxGenericDTO(fromXlsxGenericDTO);
            return "Import partiel de '" + sheetName + "' réussi.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de l'import '" + sheetName + "' : " + e.getMessage();
        }
    }
}
