package com.fitlogtimer.service;

import com.fitlogtimer.config.GoogleDriveConfig;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleDriveService {

    private final Drive drive;
    private final GoogleDriveConfig googleDriveConfig;

    /**
     * Upload un fichier JSON vers Drive (identique à ton code Android)
     */
    public String uploadFile(String fileName, String jsonContent) {
        try {
            log.info("📤 Début upload vers Drive: {}", fileName);

            // FileMetadata (identique à Android)
            File fileMetadata = new File();
            fileMetadata.setName(fileName);
            fileMetadata.setMimeType("application/json");
            fileMetadata.setParents(Collections.singletonList(googleDriveConfig.getFolderId()));

            // MediaContent
            ByteArrayContent mediaContent = new ByteArrayContent(
                    "application/json",
                    jsonContent.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );

            log.info("📁 Création du fichier dans Drive...");

            // Upload (identique à Android)
            File file = drive.files().create(fileMetadata, mediaContent)
                    .setFields("id, name, webViewLink")
                    .setIgnoreDefaultVisibility(true)  // ← Même paramètre qu'Android
                    .execute();

            log.info("✅ Fichier créé ID: {}", file.getId());

            // Rendre le fichier public (identique à Android)
            Permission permission = new Permission()
                    .setType("anyone")
                    .setRole("reader");

            drive.permissions().create(file.getId(), permission).execute();
            log.info("🔓 Permissions publiques appliquées");

            return file.getId();

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'upload vers Drive", e);
            throw new RuntimeException("Erreur upload Drive: " + e.getMessage(), e);
        }
    }

    /**
     * Vérifie la connexion à Drive
     */
    public boolean testConnection() {
        try {
            drive.files().list().setPageSize(1).execute();
            log.info("✅ Connexion Google Drive OK");
            return true;
        } catch (Exception e) {
            log.error("❌ Erreur connexion Google Drive", e);
            return false;
        }
    }
}
