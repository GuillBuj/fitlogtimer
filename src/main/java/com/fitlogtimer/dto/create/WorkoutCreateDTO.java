package com.fitlogtimer.dto.create;

import java.time.LocalDate;

public record WorkoutCreateDTO(
    LocalDate date,
    double bodyWeight,
    String comment,
    String type){
}
