package com.fitlogtimer.dto.stats;

import java.util.Map;

public record CombinedMultiYearDTO(
        int nbReps,
        MaxWeightWith1RMAndDateDTO personalBest,
        Map<Integer, YearlyBestWithTrendDTO> bestsByYear
) {
    public MaxWeightWith1RMAndDateDTO getYearlyBest(int year) {
        YearlyBestWithTrendDTO yearlyBestWithTrend = bestsByYear.get(year);
        return (yearlyBestWithTrend != null) ? yearlyBestWithTrend.maxWeightPlus() : null;
    }

//    public Trend getYearlyTrend(int year) {
//        Trend trend = bestsByYear.yearlyTrend();
//    }
}
