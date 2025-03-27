package com.fitlogtimer.dto.workoutDisplay;

import java.time.LocalDate;
import java.util.List;

import com.fitlogtimer.dto.SetsGroupedFinalDTO;


public record WorkoutGroupedDTO(
    int id,
    LocalDate date,
    double bodyWeight,
    String comment,
    List<SetsGroupedFinalDTO> exerciseSets
) {
    
}
