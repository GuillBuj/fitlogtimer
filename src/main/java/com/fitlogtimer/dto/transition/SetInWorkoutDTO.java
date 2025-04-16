package com.fitlogtimer.dto.transition;

public record SetInWorkoutDTO(
        int id,
        int exercise_id,
        double weight,
        int repNumber,
        String comment,
        String type) {
}
