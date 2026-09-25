package com.fitlogtimer.dto.stats;

public record PeriodMaxWithTrendDTO(
        Double maxValue,
        Double bodyweight,
        int workoutId,
        int year,
        Integer semester,
        Integer quarter,
        Double trendRatio,
        String trendColor,
        Double absoluteRatio,
        String absoluteColor,
        Integer absoluteRanking
) {
    public PeriodMaxWithTrendDTO(
            Double maxValue,
            Double bodyweight,
            int workoutId,
            int year,
            Double trendRatio,
            String trendCcolor,
            Double absoluteRatio,
            String absoluteColor,
            Integer absoluteRanking) {
        this(maxValue, bodyweight, workoutId, year, null, null, trendRatio, trendCcolor, absoluteRatio, absoluteColor, absoluteRanking);
    }
}
