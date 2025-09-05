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
    public ExerciseSetWithBodyWeightAndDateDTO {
        // Arrondir le ratio à 4 décimales si il n'est pas null
        ratio = Math.round(ratio * 1000.0) / 1000.0;
    }
}
