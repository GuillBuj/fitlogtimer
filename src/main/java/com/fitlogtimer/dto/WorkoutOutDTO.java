package com.fitlogtimer.dto;

import java.time.LocalDate;

public record WorkoutOutDTO(
    int id,
    LocalDate date,
    double bodyWeight,
    String comment){ 
}
