package com.fitlogtimer.dto;

import java.time.LocalDate;

public record ExerciseSetFor1RMCalcDTO(
        double weight,
        int reps,
        double bodyWeight,
        int workoutId,
        LocalDate date
) {}
