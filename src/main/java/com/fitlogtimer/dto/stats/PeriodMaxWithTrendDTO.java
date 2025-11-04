package com.fitlogtimer.dto.stats;

public record PeriodMaxWithTrendDTO(
        Double maxValue,
        Double bodyweight,
        int workoutId,
        int year,
        Integer semester,
        Integer quarter,
        Double trendRatio,
        String color
) {
    public PeriodMaxWithTrendDTO(
            Double maxValue,
            Double bodyweight,
            int workoutId,
            int year,
            Double trendRatio,
            String color) {
        this(maxValue, bodyweight, workoutId, year, null, null, trendRatio, color);
    }
}
