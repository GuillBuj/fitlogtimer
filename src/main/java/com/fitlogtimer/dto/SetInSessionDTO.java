package com.fitlogtimer.dto;

public record SetInSessionDTO(
        Long id,
        int exercise_id,
        double weight,
        int repNumber,
        boolean isMax,
        String comment) {
}
