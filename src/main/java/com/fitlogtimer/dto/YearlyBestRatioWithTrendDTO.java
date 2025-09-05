package com.fitlogtimer.dto;

import com.fitlogtimer.dto.stats.MaxWeightWith1RMAndDateDTO;
import com.fitlogtimer.enums.Trend;

public record YearlyBestRatioWithTrendDTO(
        ExerciseSetWithBodyWeightAndDateDTO bestRatio,
        Trend trend
) {
}
