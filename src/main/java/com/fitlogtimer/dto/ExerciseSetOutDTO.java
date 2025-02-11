package com.fitlogtimer.dto;

public record ExerciseSetOutDTO(
        Long id,
        int exercise_id,
        double weight,
        int repNumber,
        boolean isMax,
        String comment,
        Long session_id) {
}
