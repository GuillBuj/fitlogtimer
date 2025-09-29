package com.fitlogtimer.controller;

import com.fitlogtimer.service.GoogleDriveService;
import com.fitlogtimer.service.JsonExercisesForAndroidService;
import com.google.api.services.drive.Drive;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
public class OAuth2CallbackController {

    private final GoogleDriveService googleDriveService;
    private final JsonExercisesForAndroidService jsonExercisesForAndroidService;

    /** Callback OAuth2 pour l'export (à la racine) */
    @GetMapping("/oauth2callback")
    public String oauth2callback(@RequestParam(value = "code", required = false) String code,
                                 @RequestParam(value = "error", required = false) String error,
                                 RedirectAttributes redirectAttributes) throws Exception {

        if (error != null || code == null) {
            redirectAttributes.addFlashAttribute("successRedMessage", "Erreur d'autorisation Google Drive");
            return "redirect:/drive-json/files";
        }

        try {
            Drive driveService = googleDriveService.getDriveServiceWithCode(code);
            String json = jsonExercisesForAndroidService.createJsonForAndroid();
            googleDriveService.uploadJsonToDrive(driveService, json, "exercises.json");
            redirectAttributes.addFlashAttribute("successMessage", "Export JSON réussi vers Google Drive !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("successRedMessage", "Erreur lors de l'export: " + e.getMessage());
        }

        return "redirect:/drive-json/files";
    }
}
