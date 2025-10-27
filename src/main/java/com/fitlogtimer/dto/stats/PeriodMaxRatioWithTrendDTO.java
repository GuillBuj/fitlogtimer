package com.fitlogtimer.dto.stats;

public record PeriodMaxRatioWithTrendDTO(
        Double maxValue,
        Double bodyweight,
        Double ratio,
        int workoutId,
        int year,
        Double trendRatio,
        String color
) {}
