package com.fitlogtimer.dto.listitem;

public record SetWorkoutListItemDTO(
        int id,
        String exerciseNameShort,
        double weight,
        String bands,
        int repNumber,
        int durationS,
        String distance,
        String comment,
        String type) {
}
