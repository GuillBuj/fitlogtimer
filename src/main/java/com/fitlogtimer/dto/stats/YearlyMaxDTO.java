package com.fitlogtimer.dto.stats;

import java.time.LocalDate;

public record YearlyMaxDTO(
        int year,
        double maxWeight,
        double RMest,
        LocalDate date,  // On conserve la date
        boolean hasData
) {
}
