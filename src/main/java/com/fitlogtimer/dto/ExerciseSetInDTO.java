package com.fitlogtimer.dto;

public record ExerciseSetInDTO(
        Long exercise_id,
        double weight,
        int repNumber,
        boolean isMax,
        String comment,
        Long session_id) {
}
