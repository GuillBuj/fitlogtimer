package com.fitlogtimer.dto.stats;

public record PeriodMaxDTO(
        Double maxValue,
        Double bodyweight,
        int workoutId,
        int year,
        Integer semester,
        Integer quarter
) {
    public PeriodMaxDTO(Double maxValue, Double bodyweight, int workoutId, int year, Integer semester, Integer quarter) {
        this.maxValue = maxValue;
        this.bodyweight = bodyweight;
        this.workoutId = workoutId;
        this.year = year;
        this.semester = semester;
        this.quarter = quarter;
    }
}
