package com.fitlogtimer.util.mapperhelper;

import org.mapstruct.Named;
import org.springframework.stereotype.Service;

import com.fitlogtimer.constants.ExerciseSetType;
import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.Workout;
import com.fitlogtimer.model.sets.ElasticSet;
import com.fitlogtimer.model.sets.FreeWeightSet;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.repository.WorkoutRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ExerciseSetMappingHelper {
    private final ExerciseRepository exerciseRepository;
    private final WorkoutRepository workoutRepository;

    @Named("createExerciseSet")
    public ExerciseSet createExerciseSetFromDTO(ExerciseSetCreateDTO dto) {
        ExerciseSet exerciseSet = switch (dto.type()) {
            case ExerciseSetType.FREE_WEIGHT -> {
                FreeWeightSet set = new FreeWeightSet();
                set.setWeight(dto.weight());
                set.setRepNumber(dto.repNumber());
                set.setComment(dto.comment());
                yield set;
            }
            case ExerciseSetType.ELASTIC -> {
                ElasticSet set = new ElasticSet();
                set.setBands(dto.bands());
                set.setRepNumber(dto.repNumber());
                set.setComment(dto.comment());
                yield set;
            }
            default -> throw new IllegalArgumentException("Type inconnu : " + dto.type());
        };

        exerciseSet.setRepNumber(dto.repNumber());
        exerciseSet.setComment(dto.comment());
        exerciseSet.setTag(dto.tag()); // si présent dans le DTO
        exerciseSet.setExercise(findExerciseOrThrow(dto.exercise_id()));
        exerciseSet.setWorkout(getWorkoutOrThrow(dto.workout_id()));

        return exerciseSet;
    }
    
    public Exercise findExerciseOrThrow(int exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
    }

    public Workout getWorkoutOrThrow(int workoutId) {
        return workoutRepository.findById(workoutId)
                .orElseThrow(() -> new IllegalArgumentException("Workout not found"));
    }
}
