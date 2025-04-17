package com.fitlogtimer.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.Workout;
import com.fitlogtimer.model.sets.ElasticSet;
import com.fitlogtimer.util.mapperhelper.ExerciseSetMappingHelper;

@Mapper(componentModel = "spring")
public interface ElasticSetMapper {

    @Mapping(target = "id", ignore = true)
    ElasticSet toElasticSet(ExerciseSetCreateDTO dto, ExerciseSetMappingHelper helper);

    @Named("resolveExercise")
    default Exercise resolveExercise(int exerciseId, @Context ExerciseSetMappingHelper helper) {
        return helper.findExerciseOrThrow(exerciseId);
    }

    @Named("resolveWorkout")
    default Workout resolveWorkout(int workoutId, @Context ExerciseSetMappingHelper helper) {
        return helper.getWorkoutOrThrow(workoutId);
    }
}
