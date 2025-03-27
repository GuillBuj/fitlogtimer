package com.fitlogtimer.dto;

import java.time.LocalDate;

public record WorkoutInDTO(
    LocalDate date,
    double bodyWeight,
    String comment){ 
}
