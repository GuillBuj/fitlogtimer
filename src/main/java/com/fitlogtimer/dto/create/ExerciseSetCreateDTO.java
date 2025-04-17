package com.fitlogtimer.dto.create;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ExerciseSetCreateDTO(
        @Positive int exercise_id,
        @PositiveOrZero double weight,
        @Positive int repNumber,
        @Size(max=100) String bands,
        String tag,
        @Size(max=100) String comment,
        @Positive int workout_id,
        String type) {
}
