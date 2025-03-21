package com.fitlogtimer.mapper;

import org.springframework.stereotype.Component;

import com.fitlogtimer.dto.ExerciseCreateDTO;
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
    
}