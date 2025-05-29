package com.fitlogtimer.dto.create;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ExerciseSetCreateDTO(
        @Positive int exercise_id,
        @PositiveOrZero double weight,
        @Positive int repNumber,
        @Size(max=100) String bands,
        @PositiveOrZero int durationS,
        @Size(max=100) String distance,
        String tag,
        @Size(max=100) String comment,
        @Positive int workout_id,
        String type) {

    public ExerciseSetCreateDTO withWorkoutId(int newWorkoutId) {
        return new ExerciseSetCreateDTO(
                exercise_id,
                weight,
                repNumber,
                bands,
                durationS,
                distance,
                tag,
                comment,
                newWorkoutId,
                type
        );
    }
}
