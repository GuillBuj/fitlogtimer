package com.fitlogtimer.dto.transition;

import com.fitlogtimer.enums.SetMode;

public record SetInWorkoutDTO(
        int id,
        int exercise_id,
        double weight,
        String bands,
        int repNumber,
        int durationS,
        String distance,
        String comment,
        String type,
        SetMode setMode) {
}
