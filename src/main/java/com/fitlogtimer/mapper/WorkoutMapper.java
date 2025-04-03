package com.fitlogtimer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fitlogtimer.dto.create.WorkoutCreateDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDCHeavyDTO;
import com.fitlogtimer.model.Workout;

@Mapper(componentModel = "spring")
public interface WorkoutMapper {

    public Workout toEntity(WorkoutCreateDTO dto);

    @Mapping(target = "type", constant = "HEAVY")
    @Mapping(target = "tagImport", constant = "importH")
    public Workout toEntity(FromXlsxDCHeavyDTO dto);
}
