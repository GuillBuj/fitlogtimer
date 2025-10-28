package com.fitlogtimer.dto.stats;

public record PeriodMax1RMEstDTO(
        Double maxValue,
        int nbReps,
        Double bodyweight,
        Double estimated1RM,
        int workoutId,
        int year
) {
    public PeriodMax1RMEstDTO(Double maxValue, int nbReps,Double bodyweight, Double estimated1RM, int workoutId, int year) {
        this.maxValue = maxValue;
        this.nbReps = nbReps;
        this.bodyweight = bodyweight;
        this.estimated1RM = estimated1RM;
        this.workoutId = workoutId;
        this.year = year;
    }
}
