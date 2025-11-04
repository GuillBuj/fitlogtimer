package com.fitlogtimer.dto.stats;

import java.util.Map;

public record ExerciseYearlyMax1RMEstTableDTO(
        String exerciseName,
        int exerciseId,
        Map<String, PeriodMax1RMEstWithTrendDTO> periodData
) {
}
