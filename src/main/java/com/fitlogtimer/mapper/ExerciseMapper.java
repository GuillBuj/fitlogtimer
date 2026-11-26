package com.fitlogtimer.mapper;

import com.fitlogtimer.dto.display.ExerciseDisplayDTO;
import com.fitlogtimer.dto.listitem.ExerciseListItemDTO;
import com.fitlogtimer.dto.update.ExerciseUpdateDTO;
import com.fitlogtimer.enums.Family;
import org.mapstruct.Mapper;

import com.fitlogtimer.dto.create.ExerciseCreateDTO;
import com.fitlogtimer.model.Exercise;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

    @Mapping(target = "family", source = "family", qualifiedByName = "stringToFamily")
    Exercise toEntity(ExerciseCreateDTO dto);

    ExerciseUpdateDTO toExerciseUpdateDTO(Exercise exercise);

    Exercise toEntity(ExerciseUpdateDTO exerciseUpdateDTO);

    Exercise toEntity(ExerciseListItemDTO exerciseListItemDTO);

    @Mapping(target = "name", source = "exercise.shortName")
    ExerciseDisplayDTO toExerciseDisplayDTO(Exercise exercise);

    @Named("stringToFamily")
    default Family stringToFamily(String code) {
        return Family.fromCode(code);
    }
}