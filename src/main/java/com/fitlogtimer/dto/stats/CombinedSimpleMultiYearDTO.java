package com.fitlogtimer.dto.stats;

import java.util.Map;

public record CombinedSimpleMultiYearDTO(
        MaxWithDateDTO personalBest,
        Map<Integer, YearlyBestSimpleWithTrendDTO> bestsByYear
) {
    public MaxWithDateDTO getYearlyBest(int year) {
        YearlyBestSimpleWithTrendDTO yearlyBestSimpleWithTrend = bestsByYear.get(year);
        return (yearlyBestSimpleWithTrend != null) ? yearlyBestSimpleWithTrend.maxPlus() : null;
    }

}
