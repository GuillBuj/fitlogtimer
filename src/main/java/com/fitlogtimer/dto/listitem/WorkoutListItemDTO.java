package com.fitlogtimer.dto.listitem;

import java.time.LocalDate;

public record WorkoutListItemDTO(
    int id,
    LocalDate date,
    double bodyWeight,
    String type,
    String comment){ 
}
