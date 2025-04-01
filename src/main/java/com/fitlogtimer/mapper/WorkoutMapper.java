package com.fitlogtimer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fitlogtimer.dto.FromXlsxDCHeavyDTO;
import com.fitlogtimer.dto.WorkoutInDTO;
import com.fitlogtimer.model.Workout;

@Mapper(componentModel = "spring")
public interface WorkoutMapper {

    public Workout toEntity(WorkoutInDTO dto);

    @Mapping(target = "type", constant = "HEAVY")
    @Mapping(target = "tagImport", constant = "importH")
    public Workout toEntity(FromXlsxDCHeavyDTO dto);
}
