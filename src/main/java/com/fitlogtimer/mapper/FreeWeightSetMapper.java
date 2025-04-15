package com.fitlogtimer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.Workout;
import com.fitlogtimer.model.sets.FreeWeightSet;

@Mapper(componentModel = "spring")
public interface FreeWeightSetMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercise", source = "exercise")
    @Mapping(target = "workout", source = "workout")
    @Mapping(target = "comment", source = "exerciseSetCreateDTO.comment")
    FreeWeightSet fromExerciseSetCreateDTO(ExerciseSetCreateDTO exerciseSetCreateDTO, Exercise exercise, Workout workout);
}
