package com.fitlogtimer.dto;

import com.fitlogtimer.model.Exercise;

import java.time.LocalDate;

public record ExerciseSetWithBodyWeightAndDateDTO(
        int id,
        Exercise exercise,
        int repNumber,
        double weight,
        double bodyWeight,
        int idWorkout,
        LocalDate workoutDate,
        double ratio
) {
}
