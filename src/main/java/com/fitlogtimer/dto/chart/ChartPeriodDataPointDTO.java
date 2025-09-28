package com.fitlogtimer.dto.chart;

import com.fitlogtimer.enums.RecordType;

public record ChartPeriodDataPointDTO(
        String period,  // format "YYYY-MM"
        String exerciseName,
        Double max,
        RecordType recordType

) {
}
