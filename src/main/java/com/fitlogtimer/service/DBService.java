package com.fitlogtimer.service;

import org.springframework.stereotype.Service;

import com.fitlogtimer.repository.ExerciseSetRepository;
import com.fitlogtimer.repository.WorkoutRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DBService {
    
    private final ExerciseSetRepository exerciseSetRepository;
    private final WorkoutRepository workoutRepository;

    public void clearExerciseSetAndWorkout(){
        exerciseSetRepository.deleteAllExerciseSets();
        workoutRepository.deleteAllWorkouts();        
        
        workoutRepository.resetWorkoutId();
        exerciseSetRepository.resetExerciseSetId();
    }    

}
