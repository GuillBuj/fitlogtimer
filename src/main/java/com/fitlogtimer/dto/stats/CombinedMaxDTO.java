package com.fitlogtimer.dto.stats;

public record CombinedMaxDTO(
        int nbReps,
        MaxWeightWith1RMAndDateDTO personalBest,
        MaxWeightWith1RMAndDateDTO seasonBest
) {
}
