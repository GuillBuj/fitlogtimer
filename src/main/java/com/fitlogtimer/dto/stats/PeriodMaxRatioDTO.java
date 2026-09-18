package com.fitlogtimer.dto.stats;

public record PeriodMaxRatioDTO(
        Double maxValue,
        Double bodyweight,
        Double ratio,
        int workoutId,
        int year,
        Integer semester,
        Integer quarter,
        Integer month
) {
    public PeriodMaxRatioDTO(Double maxValue, Double bodyweight, Double ratio, int workoutId, int year, Integer semester, Integer quarter, Integer month) {
        this.maxValue = maxValue;
        this.bodyweight = bodyweight;
        this.ratio = ratio;
        this.workoutId = workoutId;
        this.year = year;
        this.semester = semester;
        this.quarter = quarter;
        this.month = month;
    }
}
