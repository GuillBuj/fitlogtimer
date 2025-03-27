package com.fitlogtimer.mapper;

import org.springframework.stereotype.Component;

import com.fitlogtimer.dto.WorkoutInDTO;
import com.fitlogtimer.model.Workout;

@Component
public class WorkoutMapper {
    
    public Workout toWorkout(WorkoutInDTO workoutInDTO){
        
        Workout workout = new Workout();
        workout.setDate(workoutInDTO.date());
        workout.setBodyWeight(workoutInDTO.bodyWeight());
        workout.setComment(workoutInDTO.comment());

        return workout;
    }
}
