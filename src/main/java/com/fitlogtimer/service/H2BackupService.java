package com.fitlogtimer.service;

import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
@AllArgsConstructor
@Slf4j
public class H2BackupService {

//    private final DataSource dataSource;
//
//    @PreDestroy
//    public void backUpDatabase() {
//        log.info("Sauvegarde BDD avant arrêt");
//        String filename = String.format("./backups/backup_%s.sql", java.time.LocalDate.now());
//        try(Connection connection = dataSource.getConnection();
//            Statement statement = connection.createStatement()
//        ) {
//            statement.execute("SCRIPT TO '" + filename + "'");
//            log.info("Sauvegarde effectuée avec succès.");
//        } catch (SQLException e) {
//            log.error("Erreur lors de la sauvegarde de la BDD", e);
//            throw new RuntimeException(e);
//        }
//    }

}
