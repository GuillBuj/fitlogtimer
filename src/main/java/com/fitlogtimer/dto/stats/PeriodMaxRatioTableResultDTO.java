package com.fitlogtimer.dto.stats;

import java.util.List;
import java.util.Map;

public record PeriodMaxRatioTableResultDTO(
        List<ExercisePeriodMaxRatioTableDTO> table,
        Map<String, PeriodBig4DTO> big4Data
) {}
