package com.fitlogtimer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fitlogtimer.dto.create.WorkoutCreateDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDCHeavyDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDCLightDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDCVarDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDeadliftDTO;
import com.fitlogtimer.model.Workout;

@Mapper(componentModel = "spring")
public interface WorkoutMapper {

    public Workout toEntity(WorkoutCreateDTO dto);

    @Mapping(target = "type", constant = "HEAVY")
    @Mapping(target = "tagImport", constant = "importH")
    public Workout toEntity(FromXlsxDCHeavyDTO dto);

    @Mapping(target = "type", constant = "DL")
    @Mapping(target = "tagImport", constant = "importDL")
    public Workout toEntity(FromXlsxDeadliftDTO dto);

    @Mapping(target = "type", constant = "LIGHT")
    @Mapping(target = "tagImport", constant = "importL")
    public Workout toEntity(FromXlsxDCLightDTO dto);

    @Mapping(target = "type", constant = "VAR")
    @Mapping(target = "tagImport", constant = "importV")
    public Workout toEntity(FromXlsxDCVarDTO dto);
}
