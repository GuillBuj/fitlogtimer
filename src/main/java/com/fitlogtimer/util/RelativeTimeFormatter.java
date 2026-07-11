package com.fitlogtimer.util;

import com.fitlogtimer.dto.time.RelativeTimeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

public final class RelativeTimeFormatter {

    private RelativeTimeFormatter() {
        // Empêche l'instanciation
    }

    public static RelativeTimeDTO format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return new RelativeTimeDTO(
                    "Aucun téléchargement",
                    "red"
            );
        }

        Duration duration = Duration.between(dateTime, LocalDateTime.now());

        if (duration.toMinutes() < 1) {
            return new RelativeTimeDTO("Il y a moins d'une minute",
                    "green");
        }

        if (duration.toHours() < 1) {
            long minutes = duration.toMinutes();
            return new RelativeTimeDTO(
                    "Il y a " + minutes + " minute" + (minutes > 1 ? "s" : ""),
                    "yellow");
        }

        long hours = duration.toHours();
        if (duration.toDays() < 1) {
            return new RelativeTimeDTO(
                    "Il y a " + hours + " heure" + (hours > 1 ? "s" : ""),
                    "red");
        }

        long days = duration.toDays();
        return new RelativeTimeDTO(
                "Il y a " + days + " jour" + (days > 1 ? "s" : ""),
                "red");
    }

}
