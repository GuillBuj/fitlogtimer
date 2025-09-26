package com.fitlogtimer.dto.chart;

public record ChartPeriodDataPointDTO(
        String period,  // format "YYYY-MM"
        String exerciseName,
        Double max,
        Double est1RMmax

) {
}
