package com.fitlogtimer.mapper;

import com.fitlogtimer.dto.display.ExerciseDisplayDTO;
import com.fitlogtimer.dto.listitem.ExerciseListItemDTO;
import com.fitlogtimer.dto.update.ExerciseUpdateDTO;
import org.mapstruct.Mapper;

import com.fitlogtimer.dto.create.ExerciseCreateDTO;
import com.fitlogtimer.model.Exercise;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

    Exercise toEntity(ExerciseCreateDTO exerciseCreateDTO);

    ExerciseUpdateDTO toExerciseUpdateDTO(Exercise exercise);

    Exercise toEntity(ExerciseUpdateDTO exerciseUpdateDTO);

    Exercise toEntity(ExerciseListItemDTO exerciseListItemDTO);

    @Mapping(target = "name", source = "exercise.shortName")
    ExerciseDisplayDTO toExerciseDisplayDTO(Exercise exercise);
}