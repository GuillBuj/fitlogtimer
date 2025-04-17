package com.fitlogtimer.dto.listitem;

public record SetWorkoutListItemDTO(
        int id,
        String exerciseNameShort,
        double weight,
        String bands,
        int repNumber,
        int durationS,
        String comment,
        String type) {
}
