package com.fitlogtimer.dto;

public record SetInSessionDTO(
        int id,
        int exercise_id,
        double weight,
        int repNumber,
        boolean isMax,
        String comment) {
}
