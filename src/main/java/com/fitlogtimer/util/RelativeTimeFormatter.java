package com.fitlogtimer.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

public final class RelativeTimeFormatter {

    private RelativeTimeFormatter() {
        // Empêche l'instanciation
    }

    public static String format(LocalDateTime dateTime) {
        Duration duration = Duration.between(dateTime, LocalDateTime.now());
        if (duration.toMinutes() < 1) {
            return "Il y a moins d'une minute";
        }
        if (duration.toHours() < 1) {
            long minutes = duration.toMinutes();
            return "Il y a " + minutes + " minute" + (minutes > 1 ? "s" : "");
        }
        if (duration.toDays() < 1) {
            long hours = duration.toHours();
            return "Il y a " + hours + " heure" + (hours > 1 ? "s" : "");
        }

        long days = duration.toDays();
        return "Il y a " + days + " jour" + (days > 1 ? "s" : "");
    }

}
