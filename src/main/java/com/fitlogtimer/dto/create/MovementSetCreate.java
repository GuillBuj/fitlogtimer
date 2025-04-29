package com.fitlogtimer.dto.create;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MovementSetCreate(
        @Positive int exercise_id,
        @Positive int repNumber,
        @Size(min=1, max = 100) String distance,
        @Size(min=1, max = 100) String bands,        
        @Positive double weight,
        @Size(max=100) String comment,
        @Positive int workout_id) {
}

