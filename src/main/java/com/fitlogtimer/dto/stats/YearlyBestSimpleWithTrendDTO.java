package com.fitlogtimer.dto.stats;

import com.fitlogtimer.enums.Trend;

public record YearlyBestSimpleWithTrendDTO(
        MaxWithDateDTO maxPlus,
        Trend trend
) {
}
