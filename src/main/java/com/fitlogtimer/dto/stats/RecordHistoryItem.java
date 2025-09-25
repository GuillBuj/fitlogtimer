package com.fitlogtimer.dto.stats;

import java.time.LocalDate;

public record RecordHistoryItem(
        double weight,
        int nbReps,
        int durationS,
        LocalDate date,
        double bodyWeight,
        int workoutId
) {
}
