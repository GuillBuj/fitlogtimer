package com.fitlogtimer.dto.stats;

import java.time.LocalDate;

public record TopFreeWeightSetsItemDTO(
        double weight,
        int nbReps,
        LocalDate date,
        double bodyWeight,
        int workoutId
) {
    public TopFreeWeightSetsItemDTO(
            double weight,
            int nbReps,
            LocalDate date,
            double bodyWeight,
            int workoutId
    ) {
        this.weight = weight;
        this.nbReps = nbReps;
        this.date = date;
        this.bodyWeight = bodyWeight;
        this.workoutId = workoutId;
    }
}
