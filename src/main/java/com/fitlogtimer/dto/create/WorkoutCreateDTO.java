package com.fitlogtimer.dto.create;

import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record WorkoutCreateDTO(
    LocalDate date,
    @PositiveOrZero
    double bodyWeight,
    String comment,
    String type){
}
