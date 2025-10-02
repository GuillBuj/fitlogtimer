package com.fitlogtimer.mapper;

import org.mapstruct.Context;
import org.mapstruct.Named;

import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.Workout;
import com.fitlogtimer.util.ExerciseSetMappingHelper;

public interface TypeSetMapper {
    @Named("resolveExercise")
    default Exercise resolveExercise(int exerciseId, @Context ExerciseSetMappingHelper helper) {
        return helper.findExerciseOrThrow(exerciseId);
    }

    @Named("resolveWorkout")
    default Workout resolveWorkout(int workoutId, @Context ExerciseSetMappingHelper helper) {
        return helper.getWorkoutOrThrow(workoutId);
    }
}
