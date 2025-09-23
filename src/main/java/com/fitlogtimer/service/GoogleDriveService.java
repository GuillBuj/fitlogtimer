package com.fitlogtimer.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;


@Slf4j
@Service
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "Fitlogtimer";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);

    // ⚠️ redirect URI configuré dans la console Google Cloud
    private static final String REDIRECT_URI = "http://localhost:8080/oauth2callback";

    private final GoogleAuthorizationCodeFlow flow;

    public GoogleDriveService() throws Exception {
        var in = getClass().getClassLoader().getResourceAsStream("credentials.json");
        if (in == null) {
            throw new IllegalStateException("credentials.json manquant dans resources");
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                clientSecrets,
                SCOPES
        ).setAccessType("offline").build();
    }

    // plus de paramètre ici
    public String getAuthorizationUrl() {
        return flow.newAuthorizationUrl()
                .setRedirectUri(REDIRECT_URI)
                .build();
    }

    // plus de paramètre ici non plus
    public Drive getDriveServiceWithCode(String code) throws Exception {
        GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                .setRedirectUri(REDIRECT_URI)
                .execute();

        Credential credential = flow.createAndStoreCredential(tokenResponse, "user");
        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void uploadJsonToDrive(Drive driveService, String jsonContent, String fileName) throws Exception {
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(fileName);
        fileMetadata.setMimeType("application/json");

        AbstractInputStreamContent contentStream =
                new ByteArrayContent("application/json", jsonContent.getBytes(StandardCharsets.UTF_8));

        com.google.api.services.drive.model.File uploadedFile = driveService.files()
                .create(fileMetadata, contentStream)
                .setFields("id, name")
                .execute();

        log.info("Fichier Drive créé: {} (ID: {})",uploadedFile.getName(), uploadedFile.getId());
    }
}



