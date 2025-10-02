package com.fitlogtimer.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.dto.create.FreeWeightSetCreateDTO;
import com.fitlogtimer.model.sets.FreeWeightSet;
import com.fitlogtimer.util.ExerciseSetMappingHelper;

@Mapper(componentModel = "spring")
public interface FreeWeightSetMapper extends TypeSetMapper{
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercise", source = "dto.exercise_id", qualifiedByName = "resolveExercise")
    @Mapping(target = "workout", source = "dto.workout_id", qualifiedByName = "resolveWorkout")
    FreeWeightSet toFreeWeightSet(ExerciseSetCreateDTO dto, @Context ExerciseSetMappingHelper helper);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercise", source = "dto.exercise_id", qualifiedByName = "resolveExercise")
    @Mapping(target = "workout", source = "dto.workout_id", qualifiedByName = "resolveWorkout")
    FreeWeightSet toFreeWeightSet(FreeWeightSetCreateDTO dto, @Context ExerciseSetMappingHelper helper);

}
