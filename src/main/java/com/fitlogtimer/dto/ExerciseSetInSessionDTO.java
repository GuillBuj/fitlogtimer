package com.fitlogtimer.dto;

public record ExerciseSetInSessionDTO(
        Long id,
        Long exercise_id,
        double weight,
        int repNumber,
        boolean isMax,
        String comment) {
}
