package com.fitlogtimer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlogtimer.repository.ExerciseSetRepository;
import com.fitlogtimer.repository.WorkoutRepository;

@Service
public class DBService {
    @Autowired
    private ExerciseSetRepository exerciseSetRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    public void clearExerciseSetAndWorkout(){
        exerciseSetRepository.deleteAllExerciseSets();
        workoutRepository.deleteAllWorkouts();        
        
        workoutRepository.resetWorkoutId();
        exerciseSetRepository.resetExerciseSetId();
    }    

}
