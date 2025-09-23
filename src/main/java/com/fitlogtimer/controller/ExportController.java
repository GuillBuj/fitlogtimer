package com.fitlogtimer.controller;

import com.fitlogtimer.service.GoogleDriveService;
import com.fitlogtimer.service.JsonExercisesForAndroidService;
import com.google.api.services.drive.Drive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ExportController {

    private final GoogleDriveService googleDriveService;
    private final JsonExercisesForAndroidService jsonService;


    @GetMapping("/export")
    public String showExportJsonToDrive() throws Exception {
        return "export";
    }

    @GetMapping("/connectDrive")
    public String connectDrive() {
        String url = googleDriveService.getAuthorizationUrl();
        return "<a href=\"" + url + "\">Autoriser Google Drive</a>";
    }

    @GetMapping("/oauth2callback")
    public String oauth2callback(@RequestParam("code") String code) throws Exception {
        Drive driveService = googleDriveService.getDriveServiceWithCode(code);
        String json = jsonService.createJsonForAndroid();
        googleDriveService.uploadJsonToDrive(driveService, json, "android_export.json");
        return "✅ Export JSON réussi vers Google Drive !";
    }
}

