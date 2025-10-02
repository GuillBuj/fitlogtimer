package com.fitlogtimer.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.model.sets.ElasticSet;
import com.fitlogtimer.util.ExerciseSetMappingHelper;

@Mapper(componentModel = "spring")
public interface ElasticSetMapper extends TypeSetMapper{

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercise", source = "dto.exercise_id", qualifiedByName = "resolveExercise")
    @Mapping(target = "workout", source = "dto.workout_id", qualifiedByName = "resolveWorkout")
    ElasticSet toElasticSet(ExerciseSetCreateDTO dto, @Context ExerciseSetMappingHelper helper);

}
