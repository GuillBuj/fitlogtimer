package com.fitlogtimer.dto.stats;

import java.util.Map;

public record ExercisePeriodMaxRatioTableDTO(
        String exerciseName,
        int exerciseId,
        Map<String, PeriodMaxRatioWithTrendDTO> periodData
) {
}
