package com.fitlogtimer.dto.stats;

import java.util.List;
import java.util.Map;

public record PeriodMax1RMEstTableResultDTO(
        List<ExerciseYearlyMax1RMEstTableDTO> table,
        Map<String, PeriodBig4DTO> big4Data
) {}