package com.fitlogtimer.dto;

public record ExerciseSetInDTO(
        int exercise_id,
        double weight,
        int repNumber,
        boolean isMax,
        String type,
        String comment,
        int workout_id) {
}
