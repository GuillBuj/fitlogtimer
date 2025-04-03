package com.fitlogtimer.mapper;

import org.mapstruct.Mapper;

import com.fitlogtimer.dto.create.ExerciseCreateDTO;
import com.fitlogtimer.model.Exercise;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {
    
    Exercise toEntity(ExerciseCreateDTO exerciseCreateDTO);
}