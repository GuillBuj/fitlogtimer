package com.fitlogtimer.dto.stats;

import com.fitlogtimer.dto.ExerciseSetWithBodyWeightAndDateFor1RMDTO;
import com.fitlogtimer.enums.Trend;

public record YearlyBestRatioFor1RMWithTrendDTO(
        ExerciseSetWithBodyWeightAndDateFor1RMDTO bestRatio,
        Trend trend
) {
}
