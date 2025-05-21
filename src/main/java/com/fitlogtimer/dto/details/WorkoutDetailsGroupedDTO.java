package com.fitlogtimer.dto.details;

import java.time.LocalDate;
import java.util.List;

import com.fitlogtimer.dto.listitem.SetGroupCleanWorkoutListItemDTO;


public record WorkoutDetailsGroupedDTO(
    int id,
    LocalDate date,
    double bodyWeight,
    String comment,
    String type,
    List<SetGroupCleanWorkoutListItemDTO> exerciseSets
) {
    
}
