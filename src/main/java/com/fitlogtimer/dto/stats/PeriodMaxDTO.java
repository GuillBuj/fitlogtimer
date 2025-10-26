package com.fitlogtimer.dto.stats;

public record PeriodMaxDTO(
        Double maxValue,
        Double bodyweight,
        int workoutId,
        int year
) {
    public PeriodMaxDTO(Double maxValue, Double bodyweight, int workoutId, int year) {
        this.maxValue = maxValue;
        this.bodyweight = bodyweight;
        this.workoutId = workoutId;
        this.year = year;
    }
}
