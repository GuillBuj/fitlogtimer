package com.fitlogtimer.dto.preference;

public record ExercisePreferenceDTO(
        int exerciseId,
        String name,
        String shortName,
        String color,
        boolean visible,
        int order
) {
}
