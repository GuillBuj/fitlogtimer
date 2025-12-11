package com.fitlogtimer.dto.stats;

public record ExerciseStatCountBasicDTO(
        int exerciseId,
        String exerciseName,
        Long setsCount,
        Long repsCount,
        Integer year //si null, all-time
) {
    //Constructeur pour stats all-time
    public ExerciseStatCountBasicDTO(
            int exerciseId,
            String exerciseName,
            Long setsCount,
            Long repsCount){
        this(exerciseId, exerciseName, setsCount, repsCount, null);
    }
}
