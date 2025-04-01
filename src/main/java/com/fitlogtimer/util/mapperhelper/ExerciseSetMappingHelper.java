package com.fitlogtimer.util.mapperhelper;

import org.springframework.stereotype.Service;

import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.Workout;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.repository.WorkoutRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ExerciseSetMappingHelper {
    private final ExerciseRepository exerciseRepository;
    private final WorkoutRepository workoutRepository;

    public Exercise findExerciseOrThrow(int exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
    }

    public Workout getWorkoutOrThrow(int workoutId) {
        return workoutRepository.findById(workoutId)
                .orElseThrow(() -> new IllegalArgumentException("Workout not found"));
    }
}
