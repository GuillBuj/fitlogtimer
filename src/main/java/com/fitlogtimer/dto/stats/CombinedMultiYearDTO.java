package com.fitlogtimer.dto.stats;

import java.util.Map;

public record CombinedMultiYearDTO(
        int nbReps,
        MaxWeightWith1RMAndDateDTO personalBest,
        Map<Integer, MaxWeightWith1RMAndDateDTO> bestsByYear
) {
    public MaxWeightWith1RMAndDateDTO getYearlyBest(int year) {
        return bestsByYear.get(year);
    }
}
