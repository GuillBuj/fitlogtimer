package com.fitlogtimer.dto;

public record ExerciseSetOutDTO(
        int id,
        int exercise_id,
        double weight,
        int repNumber,
        boolean isMax,
        String comment,
        int session_id) {
}
