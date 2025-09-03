package com.fitlogtimer.dto.stats;

import com.fitlogtimer.enums.Trend;

public record YearlyBestWithTrendDTO(
        MaxWeightWith1RMAndDateDTO maxWeightPlus,
        Trend trend
) {
}
