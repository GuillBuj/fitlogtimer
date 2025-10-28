package com.fitlogtimer.dto.stats;

import java.util.Map;

public record ExerciseYearlyMax1RMEstTableDTO(
        String exerciseName,
        int exerciseId,
        Map<Integer, PeriodMax1RMEstWithTrendDTO> yearlyData
) {
}
