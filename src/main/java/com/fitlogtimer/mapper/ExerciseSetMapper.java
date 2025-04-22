package com.fitlogtimer.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.fitlogtimer.constants.ExerciseSetType;
import com.fitlogtimer.dto.details.LastSetDTO;
import com.fitlogtimer.dto.listitem.SetWorkoutListItemDTO;
import com.fitlogtimer.dto.transition.SetInWorkoutDTO;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.Workout;
import com.fitlogtimer.model.sets.ElasticSet;
import com.fitlogtimer.model.sets.FreeWeightSet;
import com.fitlogtimer.model.sets.IsometricSet;
import com.fitlogtimer.model.sets.MovementSet;
import com.fitlogtimer.util.mapperhelper.ExerciseSetMappingHelper;

@Mapper(componentModel = "spring", uses = ExerciseSetMappingHelper.class)
public abstract class ExerciseSetMapper {
    
    //public abstract FreeWeightSet toFreeWeightEntity(ExerciseSetCreateDTO dto, @Context ExerciseSetMappingHelper helper);

    
    @Mapping(target = "exercise_id", source = "exercise.id")
    @Mapping(target = "weight", expression = "java(getWeight(exerciseSet))")
    @Mapping(target = "bands", expression = "java(getBands(exerciseSet))")
    @Mapping(target = "durationS", expression = "java(getDurationS(exerciseSet))")
    @Mapping(target = "type", expression = "java(setTypeToString(exerciseSet))")
    public abstract SetInWorkoutDTO toSetInWorkoutDTO(ExerciseSet exerciseSet);

    @Mapping(target = "exerciseNameShort", source = "exercise.shortName")
    @Mapping(target = "weight", expression = "java(getWeight(exerciseSet))")
    @Mapping(target = "bands", expression = "java(getBands(exerciseSet))")
    @Mapping(target = "durationS", expression = "java(getDurationS(exerciseSet))")
    @Mapping(target = "type", expression = "java(setTypeToString(exerciseSet))")
    public abstract SetWorkoutListItemDTO toSetListItemDTO(ExerciseSet exerciseSet);

    @Mapping(target = "exerciseId", source = "exercise.id")
    @Mapping(target = "exerciseName", source = "exercise.name")
    @Mapping(target = "bands", expression = "java(getBands(exerciseSet))")
    @Mapping(target = "durationS", expression = "java(getDurationS(exerciseSet))")
    @Mapping(target = "weight", expression = "java(getWeight(exerciseSet))")
    public abstract LastSetDTO toLastSetDTO(ExerciseSet exerciseSet);

    protected String setTypeToString(ExerciseSet exerciseSet) {
        if (exerciseSet instanceof FreeWeightSet) {
            return ExerciseSetType.FREE_WEIGHT;
        } else if (exerciseSet instanceof ElasticSet) {
            return ExerciseSetType.ELASTIC;
        } 
        return "UNKNOWN";
    }
    
    protected double getWeight(ExerciseSet exerciseSet) {
        if (exerciseSet instanceof FreeWeightSet) {
            return ((FreeWeightSet) exerciseSet).getWeight();
        }
        if (exerciseSet instanceof IsometricSet) {
            return ((IsometricSet) exerciseSet).getWeight();
        }
        return 0.0; // Pour les autres types qui n'ont pas de poids
    }

    protected String getBands(ExerciseSet exerciseSet){
        if (exerciseSet instanceof ElasticSet){
            return ((ElasticSet) exerciseSet).getBands();
        }
        if (exerciseSet instanceof MovementSet){
            return ((MovementSet)exerciseSet).getBands(); 
        }
        return "";
    }

    protected int getDurationS(ExerciseSet exerciseSet){
        if (exerciseSet instanceof IsometricSet){
            return ((IsometricSet) exerciseSet).getDurationS();
        }
        return 0;
    }

    protected String getDistance(ExerciseSet exerciseSet){
        if (exerciseSet instanceof MovementSet){
            return ((MovementSet)exerciseSet).getDistance();
        }
        return "";
    }
    
    @Named("resolveExercise")
    Exercise resolveExercise(int id, @Context ExerciseSetMappingHelper helper) {
        return helper.findExerciseOrThrow(id);
    }

    @Named("resolveWorkout")
    Workout resolveWorkout(int id, @Context ExerciseSetMappingHelper helper) {
        return helper.getWorkoutOrThrow(id);
    }
}
