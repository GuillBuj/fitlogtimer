package com.fitlogtimer.dto.stats;

public record PeriodMaxRatioDTO(
        Double maxValue,
        Double bodyweight,
        Double ratio,
        int workoutId,
        int year
) {
    public PeriodMaxRatioDTO(Double maxValue, Double bodyweight, Double ratio, int workoutId, int year) {
        this.maxValue = maxValue;
        this.bodyweight = bodyweight;
        this.ratio = ratio;
        this.workoutId = workoutId;
        this.year = year;
    }
}
