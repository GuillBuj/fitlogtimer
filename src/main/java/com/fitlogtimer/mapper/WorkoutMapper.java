package com.fitlogtimer.mapper;

import java.util.List;

import com.fitlogtimer.dto.fromxlsx.*;
import com.fitlogtimer.dto.update.WorkoutUpdateDTO;
import com.fitlogtimer.util.WorkoutTypeMapperHelper;
import org.mapstruct.*;

import com.fitlogtimer.dto.create.WorkoutCreateDTO;
import com.fitlogtimer.dto.display.WorkoutListDisplayDTO;
import com.fitlogtimer.dto.listitem.WorkoutListItemDTO;
import com.fitlogtimer.model.Workout;

@Mapper(componentModel = "spring", uses = WorkoutTypeMapperHelper.class)
public interface WorkoutMapper {

    @Mapping(target = "type", ignore = true)
    public Workout toEntity(WorkoutCreateDTO dto);

    @Mapping(target = "type", expression = "java(workoutTypeMapperHelper.map(dto.name()))")
    @Mapping(target = "tagImport",  expression = "java(\"import\" + type)")
    public Workout toEntity(FromXlsxGenericWorkoutDTO dto, String type);

    // WorkoutListItemDTO toWorkoutListItemDTOPartial(Workout workout);

    @Mapping(target = "exerciseShortNames", source = "shortNames")
    WorkoutListItemDTO toWorkoutListItemDTO(Workout workout, List<String> shortNames);

    // @Mapping(target = "exercises", source = "exerciseShortNames", qualifiedByName = "mapExercises")
    // WorkoutListDisplayDTO toWorkoutDisplayDTO(WorkoutListItemDTO dto);

    @Mapping(target = "exercises", source = "exerciseShortNames")
    WorkoutListDisplayDTO toWorkoutDisplayDTO(Workout workout, List<String> exerciseShortNames);
    
//    @Named("mapExercises")
//    static List<ExerciseDisplayDTO> mapExercises(List<String> names) {
//        if (names == null) return List.of();
//        return names.stream()
//                .map(name -> new ExerciseDisplayDTO(
//                        name,
//                        ExerciseColorConstants.getColorForExercise(name)
//                ))
//                .toList();
//    }

//    @Mapping(target = "exercises", source = "exerciseShortNames", qualifiedByName = "mapExercises")
//    WorkoutListDisplayDTO toWorkoutListDisplayDTO(Workout workout, List<String> exerciseShortNames);

//    @Named("mapExercise")
//    static ExerciseDisplayDTO mapExercise(String name) {
//        if (name == null) return null;
//        return new ExerciseDisplayDTO(
//                        name,
//                        ExerciseColorConstants.getColorForExercise(name)
//                );
//    }

    WorkoutUpdateDTO toWorkoutUpdateDTO(Workout workout);

    Workout toEntity(WorkoutUpdateDTO dto);

    //    @Mapping(target = "type", constant = "HEAVY")
//    @Mapping(target = "tagImport", constant = "importH")
//    public Workout toEntity(FromXlsxDCHeavyDTO dto);
//
//    @Mapping(target = "type", constant = "DL")
//    @Mapping(target = "tagImport", constant = "importDL")
//    public Workout toEntity(FromXlsxDeadliftDTO dto);
//
//    @Mapping(target = "type", constant = "LIGHT")
//    @Mapping(target = "tagImport", constant = "importL")
//    public Workout toEntity(FromXlsxDCLightDTO dto);
//
//    @Mapping(target = "type", constant = "VAR")
//    @Mapping(target = "tagImport", constant = "importV")
//    public Workout toEntity(FromXlsxDCVarDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "type", ignore = true)
    void updateWorkoutFromDTO(WorkoutUpdateDTO dto, @MappingTarget Workout workout);
}
