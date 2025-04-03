package com.fitlogtimer.dto.listitem;

public record SetWorkoutListItemDTO(
        int id,
        String exerciseNameShort,
        double weight,
        int repNumber,
        String comment) {
}
