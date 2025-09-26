package com.fitlogtimer.dto.chart;

import java.time.LocalDate;

public record ChartDataPoint(
        LocalDate date,
        Double est1RMmax,
        Double est1RM3bestAvg,
        Double bodyWeight
) {}
