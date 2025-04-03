package com.fitlogtimer.dto.details;

import java.time.LocalDate;
import java.util.List;

import com.fitlogtimer.dto.listitem.SetWorkoutListItemDTO;


public record WorkoutDetailsBrutDTO(
    int id,
    LocalDate date,
    double bodyWeight,
    String comment,
    List<SetWorkoutListItemDTO> exerciseSets
) {
    
}
