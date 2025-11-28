package com.fitlogtimer.dto.stats;

public record ExerciseStatCountBasicDTO(
        int exerciseId,
        String exerciseName,
        Long setsCount,
        Long repsCount
) {
}
