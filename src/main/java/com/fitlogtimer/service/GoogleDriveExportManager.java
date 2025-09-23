package com.fitlogtimer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleDriveExportManager {

    private final GoogleDriveService driveService;
    private final JsonExercisesForAndroidService jsonExercisesService;

    /**
     * Upload automatique après ajout/modification d'exercice
     */
    @Async
    public void uploadExercisesJsonAutomatically() {
        try {
            log.info("🔄 Début upload automatique des exercices...");

            // Générer le JSON (ta logique existante)
            String jsonContent = jsonExercisesService.exportJsonForAndroid();
            String fileName = "exercises-preferences.json";

            // Upload vers Drive
            String fileId = driveService.uploadFile(fileName, jsonContent);

            log.info("✅ Upload automatique réussi - File ID: {}", fileId);

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'upload automatique des exercices", e);
        }
    }

    /**
     * Upload manuel (pour tests)
     */
    public String uploadExercisesJsonManually() {
        try {
            log.info("👤 Upload manuel des exercices...");

            String jsonContent = jsonExercisesService.exportJsonForAndroid();
            String fileName = "exercises-preferences-" + System.currentTimeMillis() + ".json";

            String fileId = driveService.uploadFile(fileName, jsonContent);

            log.info("✅ Upload manuel réussi - File ID: {}", fileId);
            return fileId;

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'upload manuel", e);
            throw new RuntimeException("Upload manuel échoué", e);
        }
    }
}
