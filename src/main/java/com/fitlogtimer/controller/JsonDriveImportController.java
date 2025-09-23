package com.fitlogtimer.controller;

import com.fitlogtimer.model.DriveFile;
import com.fitlogtimer.service.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
@AllArgsConstructor
@RequestMapping("/import")
public class JsonDriveImportController {

    private final JsonLocalImportService importService;
    private final JsonExercisesForAndroidService jsonExercisesForAndroidService;
    private final GoogleDriveService googleDriveService;

    /** Liste tous les fichiers JSON */
    @GetMapping("/list")
    public String listFiles(Model model) throws Exception {
        List<DriveFile> files = importService.listJsonFiles();
        Set<String> imported = importService.getImportedFiles();
        model.addAttribute("files", files);
        model.addAttribute("importedFiles", imported);
        return "import-list";
    }

    /** Affiche le contenu d’un fichier JSON */
    @GetMapping("/view/{fileName}")
    public String viewFile(@PathVariable String fileName, Model model) throws Exception {
        String content = importService.downloadFileByName(fileName);
        model.addAttribute("fileName", fileName);
        model.addAttribute("content", content);
        return "import-view";
    }

    /** Import d’un fichier (version simple) */
    @PostMapping("/import/{fileId}")
    public String importFile(@PathVariable String fileId) throws Exception {
        importService.importFile(fileId);
        return "redirect:/import/list";
    }

    /** Réinitialise un fichier pour réimport */
    @PostMapping("/reset/{fileId}")
    public String resetFile(@PathVariable String fileId) throws IOException {
        importService.resetFile(fileId);
        return "redirect:/import/list";
    }

//    @GetMapping("/exportJsonToDrive")
//    public String exportJsonToDrive() throws Exception {
//        String json = jsonExercisesForAndroidService.createJsonForAndroid();
//        googleDriveService.uploadJsonToDrive(json, "android_export.json");
//        return "Export OK";
//    }

}