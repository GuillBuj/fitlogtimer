package com.fitlogtimer.dto.update;

import java.time.LocalDate;

public record WorkoutUpdateDTO(
        int id,
        LocalDate date,
        double bodyWeight,
        String comment,
        String type
) {
}
