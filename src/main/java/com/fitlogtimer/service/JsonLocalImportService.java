package com.fitlogtimer.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitlogtimer.model.DriveFile;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class JsonLocalImportService {

    private final Path importTrackerFile = Paths.get("imported-files.json");
    private final Path inboxDir = Paths.get("INBOX"); // dossier simulant le Drive
    private final Path archiveDir = Paths.get("imported-json-archive");

    private final ObjectMapper objectMapper;
    private Set<String> importedFiles = new HashSet<>();

    private final Drive driveService;
    private final String inboxFolderId = "1Ukuk_217ZUkXb3A75G2Sedi4Q5t8tMKa";

    public JsonLocalImportService() throws Exception {
        this.objectMapper = new ObjectMapper();
        this.driveService = initDriveService();
        initializeTracker();
    }

    public Drive initDriveService() throws Exception {
        FileInputStream serviceAccountStream = new FileInputStream("service-account.json");
        ServiceAccountCredentials credentials = (ServiceAccountCredentials) ServiceAccountCredentials.fromStream(serviceAccountStream)
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("SpringBoot Drive Importer")
                .build();
    }

    private void initializeTracker() throws IOException {
        if (!Files.exists(importTrackerFile)) {
            importedFiles = new HashSet<>();
            saveTracker();
        } else {
            importedFiles = objectMapper.readValue(
                    importTrackerFile.toFile(),
                    new TypeReference<Set<String>>() {}
            );
        }

        if (!Files.exists(archiveDir)) {
            Files.createDirectories(archiveDir);
        }
        if (!Files.exists(inboxDir)) {
            Files.createDirectories(inboxDir);
        }
    }

    private void saveTracker() throws IOException {
        String json = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(importedFiles);
        Files.writeString(importTrackerFile, json, StandardCharsets.UTF_8);
    }

    /** Liste tous les fichiers JSON du dossier INBOX */
    public List<DriveFile> listJsonFiles() throws Exception {
        String query = "'" + inboxFolderId + "' in parents and mimeType='application/json' and trashed=false";
        FileList result = driveService.files().list()
                .setQ(query)
                .setFields("files(id, name)")
                .execute();

        return result.getFiles().stream()
                .map(f -> new DriveFile(f.getName()))
                .collect(Collectors.toList());
    }

    /** Récupère le contenu d’un fichier JSON */
    public String getFileContent(String fileId) throws IOException {
        Path filePath = inboxDir.resolve(fileId);
        if (Files.exists(filePath)) {
            return Files.readString(filePath, StandardCharsets.UTF_8);
        }
        return "";
    }

    public String downloadFile(String fileId) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    public String downloadFileByName(String fileName) throws Exception {
        String query = "name='" + fileName + "' and '" + inboxFolderId + "' in parents and trashed=false";
        FileList result = driveService.files().list()
                .setQ(query)
                .setFields("files(id, name)")
                .execute();
        if (!result.getFiles().isEmpty()) {
            return downloadFile(result.getFiles().get(0).getId());
        }
        return "";
    }

    /** Importe un fichier */
    public void importFile(String fileName) throws Exception {
        String query = "name='" + fileName + "' and '" + inboxFolderId + "' in parents and trashed=false";
        FileList result = driveService.files().list()
                .setQ(query)
                .setFields("files(id, name)")
                .execute();
        if (!result.getFiles().isEmpty()) {
            String fileId = result.getFiles().getFirst().getId();
            if (!importedFiles.contains(fileName)) {
                String content = downloadFile(fileId);
                // TODO : parser JSON et sauvegarder en BDD

                // Archive local
                String archiveFileName = fileName + "_" + System.currentTimeMillis() + ".json";
                Files.writeString(archiveDir.resolve(archiveFileName), content);

                // Ajouter au tracker
                importedFiles.add(fileName);
                saveTracker();
            }
        }
    }

    /** Supprime un fichier du tracker pour réimport */
    public void resetFile(String fileId) throws IOException {
        if (importedFiles.remove(fileId)) {
            saveTracker();
        }
    }

    /** Retourne la liste des fichiers déjà importés */
    public Set<String> getImportedFiles() {
        return importedFiles;
    }
}

