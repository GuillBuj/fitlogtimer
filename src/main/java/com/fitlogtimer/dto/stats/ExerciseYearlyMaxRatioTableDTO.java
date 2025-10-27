package com.fitlogtimer.dto.stats;

import java.util.Map;

public record ExerciseYearlyMaxRatioTableDTO(
        String exerciseName,
        int exerciseId,
        Map<Integer, PeriodMaxRatioWithTrendDTO> yearlyData
) {
}
