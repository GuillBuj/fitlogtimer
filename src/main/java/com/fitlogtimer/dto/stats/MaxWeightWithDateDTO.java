package com.fitlogtimer.dto.stats;

import java.time.LocalDate;

public record MaxWeightWithDateDTO(
        double maxWeight,
        LocalDate date,
        Integer workoutId) {

}
