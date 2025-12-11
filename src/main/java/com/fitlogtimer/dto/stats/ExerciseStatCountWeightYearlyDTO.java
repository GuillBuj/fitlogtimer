package com.fitlogtimer.dto.stats;

import java.util.Map;

public record ExerciseStatCountWeightYearlyDTO(
        int exerciseId,
        String exerciseName,
        Map<Integer, YearlyStatCount> statsByYear
) {
}
