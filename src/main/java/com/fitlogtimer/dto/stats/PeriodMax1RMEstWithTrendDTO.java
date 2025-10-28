package com.fitlogtimer.dto.stats;

public record PeriodMax1RMEstWithTrendDTO(
        Double maxValue,
        int nbReps,
        Double bodyweight,
        Double estimated1RM,
        int workoutId,
        int year,
        Double trendRatio,
        String color
) {}
