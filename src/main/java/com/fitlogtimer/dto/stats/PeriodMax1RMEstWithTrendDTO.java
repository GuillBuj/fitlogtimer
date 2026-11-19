package com.fitlogtimer.dto.stats;

public record PeriodMax1RMEstWithTrendDTO(
        Double maxValue,
        int nbReps,
        Double bodyweight,
        Double estimated1RM,
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
