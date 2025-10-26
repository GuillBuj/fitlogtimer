package com.fitlogtimer.dto.stats;

public record PeriodMaxWithTrendDTO(
        Double maxValue,
        Double bodyweight,
        int workoutId,
        int year,
        Double trendRatio,
        String color
) {}
