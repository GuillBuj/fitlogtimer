package com.fitlogtimer.dto.stats;

import com.fitlogtimer.dto.ExerciseSetWithBodyWeightAndDateDTO;
import com.fitlogtimer.enums.Trend;

public record YearlyBestRatioWithTrendDTO(
        ExerciseSetWithBodyWeightAndDateDTO bestRatio,
        Trend trend
) {
}
