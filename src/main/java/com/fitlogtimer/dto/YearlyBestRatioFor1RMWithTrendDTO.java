package com.fitlogtimer.dto;

import com.fitlogtimer.enums.Trend;

public record YearlyBestRatioFor1RMWithTrendDTO(
        ExerciseSetWithBodyWeightAndDateFor1RMDTO bestRatio,
        Trend trend
) {
}
