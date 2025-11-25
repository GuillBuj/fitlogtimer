package com.fitlogtimer.dto.stats;

import java.time.LocalDate;

public record MaxWeightWith1RMAndDateDTO(
        double maxWeight,
        double RMest,
        LocalDate date,
        int workoutId) {

}
