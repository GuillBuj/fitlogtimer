package com.fitlogtimer.dto.create;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record FreeWeightSetCreateDTO(
        @Positive int exercise_id,
        @Positive double weight,
        @Min(1) int repNumber,
        String tag,
        @Size(max=100) String comment,
        @Positive int workout_id) {
}
