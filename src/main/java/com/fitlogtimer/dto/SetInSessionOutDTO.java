package com.fitlogtimer.dto;

public record SetInSessionOutDTO(
        int id,
        String exerciseNameShort,
        double weight,
        int repNumber,
        boolean isMax,
        String comment) {
}
