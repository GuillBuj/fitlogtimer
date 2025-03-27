package com.fitlogtimer.dto;

public record SetInWorkoutOutDTO(
        int id,
        String exerciseNameShort,
        double weight,
        int repNumber,
        boolean isMax,
        String comment) {
}
