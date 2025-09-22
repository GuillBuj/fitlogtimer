package com.fitlogtimer.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fitlogtimer.dto.fromxlsx.FromXlsxGenericWorkoutDTO;
import com.fitlogtimer.model.DriveFile;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class JsonLocalImportService {

    private final Path importTrackerFile = Paths.get("imported-files.json");
    private final Path archiveDir = Paths.get("imported-json-archive"); // Archive local
    private final Path inboxDir = Paths.get("INBOX"); // Dossier local simulé (si besoin)

    private final ObjectMapper objectMapper; // Celui configuré par Spring
    private final WorkoutService workoutService;
    private Drive driveService;
    private Set<String> importedFiles = new HashSet<>();

    private final String inboxFolderId = "1Ukuk_217ZUkXb3A75G2Sedi4Q5t8tMKa";

    @PostConstruct
    public void init() throws Exception {
        // Créer les dossiers locaux s'ils n'existent pas
        if (!Files.exists(archiveDir)) {
            Files.createDirectories(archiveDir);
        }
        if (!Files.exists(inboxDir)) {
            Files.createDirectories(inboxDir);
        }

        this.driveService = initDriveService();
        initializeTracker();
        log.info("JsonLocalImportService initialisé avec Drive et tracker");
    }

    private void initializeTracker() throws IOException {
        if (Files.exists(importTrackerFile)) {
            importedFiles = objectMapper.readValue(
                    importTrackerFile.toFile(),
                    new TypeReference<Set<String>>() {}
            );
            log.info("{} fichiers déjà importés chargés", importedFiles.size());
        } else {
            importedFiles = new HashSet<>();
            saveTracker();
            log.info("Fichier de tracking créé");
        }
    }

    private void saveTracker() throws IOException {
        String json = objectMapper.writeValueAsString(importedFiles);
        Files.writeString(importTrackerFile, json, StandardCharsets.UTF_8);
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
                // 1. Télécharger le contenu JSON
                String content = downloadFile(fileId);

                // 2. Parser JSON vers ton DTO déjà existant
                ObjectMapper objectMapper = new ObjectMapper();
                FromXlsxGenericWorkoutDTO dto =
                        objectMapper.readValue(content, FromXlsxGenericWorkoutDTO.class);

                // 3. Créer et sauvegarder le Workout en BDD (réutilisation de ta logique XLS)
                workoutService.createWorkoutFromXlsxGenericWorkoutDTO(dto, dto.name());

                // 4. Archiver le JSON (optionnel, tu peux enlever si tu veux éviter le double stockage)
                String archiveFileName = fileName + "_" + System.currentTimeMillis() + ".json";
                Files.writeString(archiveDir.resolve(archiveFileName), content);

                // 5. Marquer comme importé
                importedFiles.add(fileName);
                saveTracker();

                log.info("Fichier {} importé avec succès", fileName);
            } else {
                log.info("Fichier {} déjà importé, ignoré", fileName);
            }
        } else {
            log.warn("Fichier {} introuvable dans le dossier Drive", fileName);
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

