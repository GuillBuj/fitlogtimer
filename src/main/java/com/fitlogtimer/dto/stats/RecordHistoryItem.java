package com.fitlogtimer.dto.stats;

import java.time.LocalDate;

public record RecordHistoryItem(
        double weight,
        int nbReps,
        LocalDate date,
        double bodyWeight,
        int workoutId
) {
}
