package com.fitlogtimer.dto.stats;

public record PeriodMaxDTO(
        Double maxValue,
        Double bodyweight,
        int workoutId,
        int year,
        Integer semester,
        Integer quarter,
        Integer month
) {
    public PeriodMaxDTO(Double maxValue, Double bodyweight, int workoutId, int year, Integer semester, Integer quarter, Integer month) {
        this.maxValue = maxValue;
        this.bodyweight = bodyweight;
        this.workoutId = workoutId;
        this.year = year;
        this.semester = semester;
        this.quarter = quarter;
        this.month = month;
    }
}
