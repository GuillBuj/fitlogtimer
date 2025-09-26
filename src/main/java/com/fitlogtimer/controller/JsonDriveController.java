package com.fitlogtimer.controller;

import com.fitlogtimer.model.DriveFile;
import com.fitlogtimer.service.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
@AllArgsConstructor
@RequestMapping("/drive-json")
public class JsonDriveController {

    private final JsonLocalImportService importService;
    private final JsonExercisesForAndroidService jsonExercisesForAndroidService;
    private final GoogleDriveService googleDriveService;

    /** Export vers Google Drive */
    @GetMapping("/export")
    public String startExport() {
        String url = googleDriveService.getAuthorizationUrl();
        return "redirect:" + url;
    }

    /** Liste tous les fichiers JSON */
    @GetMapping("/files")
    public String listFiles(Model model) throws Exception {
        List<DriveFile> files = importService.listJsonFiles();
        Set<String> imported = importService.getImportedFiles();
        model.addAttribute("files", files);
        model.addAttribute("importedFiles", imported);
        return "import-list";
    }

    /** Affiche le contenu d'un fichier JSON - ANCIEN PATTERN QUI FONCTIONNE */
    @GetMapping("/view/{fileName}")
    public String viewFile(@PathVariable String fileName, Model model) throws Exception {
        String content = importService.downloadFileByName(fileName);
        model.addAttribute("fileName", fileName);
        model.addAttribute("content", content);
        return "import-view";
    }

    /** Import d'un fichier */
    @PostMapping("/import/{fileId}")
    public String importFile(@PathVariable String fileId, RedirectAttributes redirectAttributes) throws Exception {
        try {
            importService.importFile(fileId);
            redirectAttributes.addFlashAttribute("successMessage", "Fichier importé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("successRedMessage", "Erreur lors de l'import: " + e.getMessage());
        }
        return "redirect:/drive-json/files";
    }

    /** Réinitialise un fichier pour réimport */
    @PostMapping("/reset/{fileId}")
    public String resetFile(@PathVariable String fileId, RedirectAttributes redirectAttributes) throws IOException {
        try {
            importService.resetFile(fileId);
            redirectAttributes.addFlashAttribute("successMessage", "Fichier réinitialisé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("successRedMessage", "Erreur lors de la réinitialisation: " + e.getMessage());
        }
        return "redirect:/drive-json/files";
    }
}