package com.fitlogtimer.dto.stats;

import java.util.Map;

public record CombinedMaxDTO(
        int nbReps,
        MaxWeightWith1RMAndDateDTO personalBest,
        MaxWeightWith1RMAndDateDTO seasonBest
) {

}
