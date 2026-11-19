package com.fitlogtimer.dto.stats;

public record PeriodMax1RMEstDTO(
        Double maxValue,
        int nbReps,
        Double bodyweight,
        Double estimated1RM,
        int workoutId,
        int year,
        Integer semester,
        Integer quarter,
        Integer month
) {
    public PeriodMax1RMEstDTO(Double maxValue, int nbReps,Double bodyweight, Double estimated1RM, int workoutId, int year, Integer semester, Integer quarter, Integer month) {
        this.maxValue = maxValue;
        this.nbReps = nbReps;
        this.bodyweight = bodyweight;
        this.estimated1RM = estimated1RM;
        this.workoutId = workoutId;
        this.year = year;
        this.semester = semester;
        this.quarter = quarter;
        this.month = month;
    }
}
