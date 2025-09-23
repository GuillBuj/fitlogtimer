package com.fitlogtimer.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class JsonExportToDriveService {

    private final JsonExercisesForAndroidService jsonExercisesForAndroidService;
    private Drive driveService;
    private final String exportFolderId = "19LPdQQy0BvxxiDOYfBAuxX9d-ySITefw";

    @PostConstruct
    public void init() throws Exception {
        this.driveService = initDriveService();
        log.info("JsonExportToDriveService initialisé avec Drive (dossier EXPORTS)");
    }

    private Drive initDriveService() throws Exception {
        FileInputStream serviceAccountStream = new FileInputStream("service-account.json");
        ServiceAccountCredentials credentials = (ServiceAccountCredentials) ServiceAccountCredentials.fromStream(serviceAccountStream)
                .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));

        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("SpringBoot Drive Exporter")
                .build();
    }

    public void uploadJson() throws Exception {
        String json = jsonExercisesForAndroidService.exportJsonForAndroid();

        java.io.File tempFile = java.io.File.createTempFile("exercises", ".json");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(json);
        }

        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName("exercises.json");
        fileMetadata.setMimeType("application/json");

        // Très important : pointer vers ton dossier Drive partagé
        fileMetadata.setParents(Collections.singletonList(exportFolderId));


        FileContent mediaContent = new FileContent("application/json", tempFile);

        File uploaded = driveService.files()
                .create(fileMetadata, mediaContent)
                .setFields("id, webViewLink")
                .execute();

        log.info("Fichier exporté dans FORANDROID/: id={}, link={}", uploaded.getId(), uploaded.getWebViewLink());

    }
}

