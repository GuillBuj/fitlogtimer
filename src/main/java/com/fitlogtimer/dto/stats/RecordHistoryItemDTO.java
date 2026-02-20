package com.fitlogtimer.dto.stats;

import java.time.LocalDate;

public record RecordHistoryItemDTO(
        double weight,
        int nbReps,
        int durationS,
        LocalDate date,
        double bodyWeight,
        int workoutId
) {
}
