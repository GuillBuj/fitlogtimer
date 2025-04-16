package com.fitlogtimer.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.Workout;
import com.fitlogtimer.model.sets.FreeWeightSet;
import com.fitlogtimer.util.mapperhelper.ExerciseSetMappingHelper;

@Mapper(componentModel = "spring")
public interface FreeWeightSetMapper {
    
    // @Mapping(target = "id", ignore = true)
    // @Mapping(target = "exercise", source = "exercise")
    // @Mapping(target = "workout", source = "workout")
    // @Mapping(target = "comment", source = "exerciseSetCreateDTO.comment")
    // FreeWeightSet fromExerciseSetCreateDTO(ExerciseSetCreateDTO exerciseSetCreateDTO, Exercise exercise, Workout workout);

    // FreeWeightSet fromDTO(ExerciseSetCreateDTO dto, Exercise exercise, Workout workout);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercise", source = "dto.exercise_id", qualifiedByName = "resolveExercise")
    @Mapping(target = "workout", source = "dto.workout_id", qualifiedByName = "resolveWorkout")
    FreeWeightSet toFreeWeightSet(ExerciseSetCreateDTO dto, @Context ExerciseSetMappingHelper helper);

    @Named("resolveExercise")
    default Exercise resolveExercise(int exerciseId, @Context ExerciseSetMappingHelper helper) {
        return helper.findExerciseOrThrow(exerciseId);
    }

    @Named("resolveWorkout")
    default Workout resolveWorkout(int workoutId, @Context ExerciseSetMappingHelper helper) {
        return helper.getWorkoutOrThrow(workoutId);
    }
}
