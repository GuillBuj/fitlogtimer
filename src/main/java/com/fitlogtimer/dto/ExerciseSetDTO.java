package com.fitlogtimer.dto;

public record ExerciseSetDTO(
        double weight,
        int repNumber,
        boolean isMax,
        String comment,
        Long exercise_id,
        Long session_id) {
}
