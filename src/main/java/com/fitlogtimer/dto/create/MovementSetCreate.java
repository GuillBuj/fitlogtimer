package com.fitlogtimer.dto.create;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MovementSetCreate(
        @Positive int exercise_id,
        @Positive double weight,
        @Size(min=1, max = 100) String bands,
        @Size(min=1, max = 100) String distance,
        @Size(max=100) String comment,
        @Positive int workout_id) {
}

