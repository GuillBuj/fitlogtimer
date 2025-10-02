package com.fitlogtimer.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import com.fitlogtimer.constants.FileConstants;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class XlsxReader {

    // Constantes pour les valeurs par défaut
    private static final String NA = "NA";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Cette fonction lit les données d'une feuille Excel spécifiée dans une plage de cellules donnée et renvoie un tableau 2D avec les valeurs.
     * 
     * @param excelFilePath Le chemin du fichier Excel
     * @param sheetName Le nom de la feuille à lire
     * @param startRow L'indice de la première ligne à lire
     * @param startColumn L'indice de la première colonne à lire
     * @param endRow L'indice de la dernière ligne à lire
     * @param endColumn L'indice de la dernière colonne à lire
     * @return Un tableau 2D avec les valeurs des cellules dans la plage spécifiée
     * @throws IOException Si une erreur d'entrée/sortie se produit
     */
    public String[][] readSheetData(String excelFilePath, String sheetName, int startRow, int startColumn, int endRow, int endColumn) throws IOException {
        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            String[][] data = new String[endRow - startRow + 1][endColumn - startColumn + 1];

            for (int j = startColumn; j <= endColumn; j++) {
                for (int i = startRow; i <= endRow; i++) {
                    Row row = sheet.getRow(i);
                    data[i - startRow][j - startColumn] = getCellValue(row, j).orElse(NA);
                }
            }

            return data;
        }
    }

    public String[][] readSheetData(String excelFilePath, String sheetName, int startRow, int startColumn) throws IOException {
        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);

            int lastRow = sheet.getLastRowNum();
            int lastCol = 0;
            for (int i = startRow; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getLastCellNum() > lastCol) {
                    lastCol = row.getLastCellNum();
                }
            }

            return readSheetData(sheet, startRow, startColumn, lastRow, lastCol - 1);
        }
    }


    private String[][] readSheetData(Sheet sheet, int startRow, int startColumn, int endRow, int endColumn) {
        String[][] data = new String[endRow - startRow + 1][endColumn - startColumn + 1];

        for (int j = startColumn; j <= endColumn; j++) {
            for (int i = startRow; i <= endRow; i++) {
                Row row = sheet.getRow(i);
                data[i - startRow][j - startColumn] = getCellValue(row, j).orElse(NA);
            }
        }

        return data;
    }

    /**
     * Cette méthode extrait la valeur d'une cellule en fonction de son type.
     * @param row La ligne contenant la cellule
     * @param colIndex L'indice de la colonne
     * @return Un Optional contenant la valeur de la cellule ou un Optional vide si la cellule est vide
     */
    private Optional<String> getCellValue(Row row, int colIndex) {
        if (row == null) {
            return Optional.empty();
        }

        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            return Optional.empty();
        }

        switch (cell.getCellType()) {
            case STRING:
                return Optional.of(cell.getStringCellValue());
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return Optional.of(DATE_FORMAT.format(cell.getDateCellValue()));
                } else {
                    return Optional.of(String.valueOf(cell.getNumericCellValue()));
                }
            case BLANK:
                return Optional.of(NA);
            default:
                return Optional.of(NA);
        }
    }

    //affichage inversé
    public void printFormattedData(String[][] data) {
        for (int j = 0; j < data[0].length; j++) {
            System.out.print("Colonne " + (j + 1) + ": ");
            for (int i = 0; i < data.length; i++) {
                System.out.print(data[i][j] + "\t");
            }
            System.out.println(); // Nouvelle ligne après chaque colonne
        }
    }

    //inverse lignes et colonnes pour renvoyer selon le format souhaité
    public String[][] transposeArray(String[][] array) {
        int rowCount = array.length;
        int colCount = array[0].length;
    
        String[][] transposed = new String[colCount][rowCount];
    
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                transposed[j][i] = array[i][j];
            }
        }
    
        return transposed;
    }
    
    public void printSpecificData(String[][] transposedData, int columnIndex) {
        if (columnIndex >= transposedData.length) {
            System.out.println("Index de colonne hors limite.");
            return;
        }
    
        //System.out.print("Données de la colonne " + (columnIndex + 1) + ": ");
        for (int i = 0; i < transposedData[columnIndex].length; i++) {
            System.out.print(transposedData[columnIndex][i] + "\t");
        }
        System.out.println();
    }

    public int findEndRow(String[][] data) {
        for (int i = 0; i < data.length; i++) {
            String cell = data[i][0];
            if (cell != null && FileConstants.GENERIC_END_MARKERS.contains(cell.trim())) {
                return i;
            }
        }
        return data.length;
    }

    public int findEndColumn(String[][] data) {
        if (data.length == 0) return 0;
        for (int j = 0; j < data[0].length; j++) {
            String cell = data[0][j];
            if (cell != null && FileConstants.GENERIC_END_MARKERS.contains(cell.trim())) {
                return j;
            }
        }
        return data[0].length;
    }

    public String[][] trim(String[][] data, int endRow, int endCol) {
        String[][] trimmed = new String[endRow][endCol];
        for (int i = 0; i < endRow; i++) {
            System.arraycopy(data[i], 0, trimmed[i], 0, endCol);
        }
        return trimmed;
    }
}