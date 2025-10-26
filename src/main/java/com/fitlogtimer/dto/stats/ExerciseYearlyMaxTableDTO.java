package com.fitlogtimer.dto.stats;

import java.util.Map;

public record ExerciseYearlyMaxTableDTO(
        String exerciseName,
        Integer exerciseId,
        Map<Integer, PeriodMaxWithTrendDTO> yearlyData
) {
}
