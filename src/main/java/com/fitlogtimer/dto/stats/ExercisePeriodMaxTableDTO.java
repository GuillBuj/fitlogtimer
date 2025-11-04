package com.fitlogtimer.dto.stats;

import java.util.Map;

public record ExercisePeriodMaxTableDTO(
        String exerciseName,
        int exerciseId,
        Map<String, PeriodMaxWithTrendDTO> periodData
) {
}
