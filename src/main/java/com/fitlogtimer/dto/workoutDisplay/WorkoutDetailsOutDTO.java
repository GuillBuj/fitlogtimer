package com.fitlogtimer.dto.workoutDisplay;

import java.time.LocalDate;
import java.util.List;

import com.fitlogtimer.dto.SetInWorkoutOutDTO;


public record WorkoutDetailsOutDTO(
    int id,
    LocalDate date,
    double bodyWeight,
    String comment,
    List<SetInWorkoutOutDTO> exerciseSets
) {
    
}
