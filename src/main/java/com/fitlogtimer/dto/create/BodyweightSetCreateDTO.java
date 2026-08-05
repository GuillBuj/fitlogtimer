package com.fitlogtimer.dto.create;

import com.fitlogtimer.enums.SetMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BodyweightSetCreateDTO(
        @Positive int exercise_id,
        @Positive double weight,
        String bands,
        @Min(1) int repNumber,
        @Size(max=100) String comment,
        SetMode setMode,
        @Positive int workout_id) {
}
