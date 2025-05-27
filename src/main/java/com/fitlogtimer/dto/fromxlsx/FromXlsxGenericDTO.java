package com.fitlogtimer.dto.fromxlsx;

import java.util.List;

public record FromXlsxGenericDTO(
        String name,
        List<FromXlsxGenericWorkoutDTO> workouts
) {
}
