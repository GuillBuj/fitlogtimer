package com.fitlogtimer.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.fitlogtimer.constants.ExerciseSetType;
import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.dto.details.LastSetDTO;
import com.fitlogtimer.dto.listitem.SetWorkoutListItemDTO;
import com.fitlogtimer.dto.transition.SetInWorkoutDTO;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.Workout;
import com.fitlogtimer.model.sets.FreeWeightSet;
import com.fitlogtimer.util.mapperhelper.ExerciseSetMappingHelper;

@Mapper(componentModel = "spring", uses = ExerciseSetMappingHelper.class)
public abstract class ExerciseSetMapper {
    
    @Mapping(target = "exercise", source = "dto.exercise_id", qualifiedByName = "resolveExercise")
    @Mapping(target = "workout", source = "dto.workout_id", qualifiedByName = "resolveWorkout")
    public abstract FreeWeightSet toFreeWeightEntity(ExerciseSetCreateDTO dto, @Context ExerciseSetMappingHelper helper);

    
    @Mapping(target = "exercise_id", source = "exercise.id")
    @Mapping(target = "weight", expression = "java(getWeight(entity))")
    @Mapping(target = "type", expression = "java(setTypeToString(entity))")
    public abstract SetInWorkoutDTO toSetInWorkoutDTO(ExerciseSet entity);

    @Mapping(target = "exerciseNameShort", source = "exercise.shortName")
    @Mapping(target = "weight", expression = "java(getWeight(entity))")
    @Mapping(target = "type", expression = "java(setTypeToString(entity))")
    public abstract SetWorkoutListItemDTO toSetListItemDTO(ExerciseSet entity);



    @Mapping(target = "exerciseId", source = "exercise.id")
    @Mapping(target = "exerciseName", source = "exercise.name")
    @Mapping(target = "weight", expression = "java(getWeight(entity))")
    public abstract LastSetDTO toLastSetDTO(ExerciseSet entity);

    protected String setTypeToString(ExerciseSet entity) {
        if (entity instanceof FreeWeightSet) {
            return ExerciseSetType.FREE_WEIGHT;
        } /*else if (entity instanceof ElasticSet) {
            return "ELASTIC";
        } */
        return "UNKNOWN";
    }
    
    protected double getWeight(ExerciseSet entity) {
        if (entity instanceof FreeWeightSet) {
            return ((FreeWeightSet) entity).getWeight();
        }
        return 0.0; // Pour les autres types qui n'ont pas de poids
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
