package com.fitlogtimer.mapper;

import com.fitlogtimer.dto.update.ExerciseUpdateDTO;
import org.mapstruct.Mapper;

import com.fitlogtimer.dto.create.ExerciseCreateDTO;
import com.fitlogtimer.model.Exercise;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {
    
    Exercise toEntity(ExerciseCreateDTO exerciseCreateDTO);

    ExerciseUpdateDTO toExerciseUpdateDTO(Exercise exercise);

    Exercise toEntity(ExerciseUpdateDTO exerciseUpdateDTO);
}