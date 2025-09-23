package com.fitlogtimer.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.Collections;

@Getter
@Slf4j
@Configuration
public class GoogleDriveConfig {

    @Value("1Ukuk_217ZUkXb3A75G2Sedi4Q5t8tMKa")
    private String folderId;

    @Bean
    public Drive driveService() {
        try {
            log.info("🔐 Initialisation Google Drive Service...");

            InputStream credentialsStream = new ClassPathResource("service-account.json").getInputStream();

            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                    .createScoped(Collections.singleton(DriveScopes.DRIVE));


            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            Drive drive = new Drive.Builder(httpTransport, GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName("FitLogTimer-SpringBoot")
                    .build();

            log.info("✅ Google Drive Service initialisé avec succès");
            return drive;

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'initialisation de Google Drive", e);
            throw new RuntimeException("Impossible d'initialiser Google Drive", e);
        }
    }

}