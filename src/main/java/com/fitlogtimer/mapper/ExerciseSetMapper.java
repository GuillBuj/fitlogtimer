package com.fitlogtimer.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.dto.details.LastSetDTO;
import com.fitlogtimer.dto.listitem.SetWorkoutListItemDTO;
import com.fitlogtimer.dto.transition.SetInWorkoutDTO;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.Workout;
import com.fitlogtimer.model.sets.FreeWeightSet;
import com.fitlogtimer.util.mapperhelper.ExerciseSetMappingHelper;

@Mapper(componentModel = "spring", uses = ExerciseSetMappingHelper.class)
public interface ExerciseSetMapper {
    
    @Mapping(target = "exercise", source = "dto.exercise_id", qualifiedByName = "resolveExercise")
    @Mapping(target = "workout", source = "dto.workout_id", qualifiedByName = "resolveWorkout")
    FreeWeightSet toFreeWeightEntity(ExerciseSetCreateDTO dto, @Context ExerciseSetMappingHelper helper);

    @Named("resolveExercise")
    default Exercise resolveExercise(int id, @Context ExerciseSetMappingHelper helper) {
        return helper.findExerciseOrThrow(id);
    }

    @Named("resolveWorkout")
    default Workout resolveWorkout(int id, @Context ExerciseSetMappingHelper helper) {
        return helper.getWorkoutOrThrow(id);
    }

    @Mapping(target = "exercise_id", source = "exercise.id")
    SetInWorkoutDTO toSetInWorkoutDTO(ExerciseSet entity);

    @Mapping(target = "exerciseNameShort", source = "exercise.shortName")
    SetWorkoutListItemDTO toSetListItemDTO(ExerciseSet entity);

    @Mapping(target = "exerciseId", source = "exercise.id")
    @Mapping(target = "exerciseName", source = "exercise.name")
    LastSetDTO toLastSetDTO(ExerciseSet entity);

}
