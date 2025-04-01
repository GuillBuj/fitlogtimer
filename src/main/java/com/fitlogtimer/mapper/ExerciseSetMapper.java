package com.fitlogtimer.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.fitlogtimer.dto.ExerciseSetInDTO;
import com.fitlogtimer.dto.ExerciseSetOutDTO;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.Workout;
import com.fitlogtimer.util.mapperhelper.ExerciseSetMappingHelper;

@Mapper(componentModel = "spring", uses = ExerciseSetMappingHelper.class)
public interface ExerciseSetMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercise", source = "dto.exercise_id", qualifiedByName = "resolveExercise")
    @Mapping(target = "workout", source = "dto.workout_id", qualifiedByName = "resolveWorkout")
    ExerciseSet toEntity(ExerciseSetInDTO dto, @Context ExerciseSetMappingHelper helper);

    @Named("resolveExercise")
    default Exercise resolveExercise(int exerciseId, @Context ExerciseSetMappingHelper helper) {
        return helper.findExerciseOrThrow(exerciseId);
    }

    @Named("resolveWorkout")
    default Workout resolveWorkout(int workoutId, @Context ExerciseSetMappingHelper helper) {
        return helper.getWorkoutOrThrow(workoutId);
    }

    @Mapping(target = "exercise_id", source = "exercise.id")
    @Mapping(target = "workout_id", source = "workout.id")
    ExerciseSetOutDTO toExerciseSetOutDTO(ExerciseSet entity);
}
