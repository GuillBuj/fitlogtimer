package com.fitlogtimer.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.fitlogtimer.constants.ExerciseColorConstants;
import com.fitlogtimer.dto.create.WorkoutCreateDTO;
import com.fitlogtimer.dto.display.ExerciseDisplayForWorkoutListItem;
import com.fitlogtimer.dto.display.WorkoutListDisplayDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDCHeavyDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDCLightDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDCVarDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDeadliftDTO;
import com.fitlogtimer.dto.listitem.WorkoutListItemDTO;
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

    // WorkoutListItemDTO toWorkoutListItemDTOPartial(Workout workout);

    @Mapping(target = "exerciseShortNames", source = "shortNames")
    WorkoutListItemDTO toWorkoutListItemDTO(Workout workout, List<String> shortNames);

    // @Mapping(target = "exercises", source = "exerciseShortNames", qualifiedByName = "mapExercises")
    // WorkoutListDisplayDTO toWorkoutDisplayDTO(WorkoutListItemDTO dto);

    @Mapping(target = "exercises", source = "exerciseShortNames", qualifiedByName = "mapExercises")
    WorkoutListDisplayDTO toWorkoutDisplayDTO(Workout workout, List<String> exerciseShortNames);
    
    @Named("mapExercises")
    static List<ExerciseDisplayForWorkoutListItem> mapExercises(List<String> names) {
        if (names == null) return List.of();
        return names.stream()
                .map(name -> new ExerciseDisplayForWorkoutListItem(
                        name,
                        ExerciseColorConstants.getColorForExercise(name)
                ))
                .toList();
    }
}
