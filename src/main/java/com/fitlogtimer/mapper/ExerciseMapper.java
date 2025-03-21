package com.fitlogtimer.mapper;

import org.springframework.stereotype.Component;

import com.fitlogtimer.dto.ExerciseCreateDTO;
import com.fitlogtimer.dto.ExerciseListItemDTO;
import com.fitlogtimer.model.Exercise;

@Component
public class ExerciseMapper {
    
    public Exercise toExercise(ExerciseCreateDTO exerciseCreateDTO){
        Exercise exercise = new Exercise();
        
        exercise.setName(exerciseCreateDTO.name());
        exercise.setShortName(exerciseCreateDTO.shortName());
        exercise.setMuscle(exerciseCreateDTO.muscle());
        exercise.setFamily(exerciseCreateDTO.family());

        return exercise;
    }

    public ExerciseListItemDTO toExerciseListItem(Exercise exercise, double personalBest){
        return new ExerciseListItemDTO(exercise.getId(),exercise.getName(),exercise.getShortName(),exercise.getMuscle(),exercise.getFamily(),personalBest);
    }

}