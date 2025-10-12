package com.fitlogtimer.dto.chart;

import java.time.LocalDate;

public record ChartDataPointDTO(
        LocalDate date,
        Double est1RMmax,
        Double est1RM3bestAvg,
        Double maxWeight,
        Double bodyWeight
) {}
