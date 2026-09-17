package com.fitlogtimer.dto.stats;

import java.util.List;
import java.util.Map;

public record PeriodMaxTableResultDTO(
        List<ExercisePeriodMaxTableDTO> table,
        Map<String, PeriodBig4DTO> big4Data
) {
}
