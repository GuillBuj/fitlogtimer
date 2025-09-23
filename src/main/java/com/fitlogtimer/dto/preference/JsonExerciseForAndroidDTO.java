package com.fitlogtimer.dto.preference;

public record JsonExerciseForAndroidDTO(
        int id,
        String name,
        String shortName,
        int position,
        double defaultWeight,
        int defaultReps,
        String type
) {
}
