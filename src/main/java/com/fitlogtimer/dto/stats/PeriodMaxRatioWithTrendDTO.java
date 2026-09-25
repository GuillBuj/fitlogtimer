package com.fitlogtimer.dto.stats;

public record PeriodMaxRatioWithTrendDTO(
        Double maxValue,
        Double bodyweight,
        Double ratio,
        int workoutId,
        int year,
        Integer semester,
        Integer quarter,
        Integer month,
        Double trendRatio,
        String trendColor,
        Double absoluteRatio,
        String absoluteColor,
        Integer absoluteRanking
) {}
