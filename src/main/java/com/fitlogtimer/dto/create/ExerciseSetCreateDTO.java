package com.fitlogtimer.dto.create;

public record ExerciseSetCreateDTO(
        int exercise_id,
        double weight,
        int repNumber,
        String type,
        String comment,
        int workout_id) {
}
