package com.fitlogtimer.mapper;

import org.springframework.stereotype.Component;

import com.fitlogtimer.constants.ExerciseSetType;
import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.Workout;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.repository.WorkoutRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ExerciseSetFacadeMapper {
    private final FreeWeightSetMapper freeWeightSetMapper;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutRepository workoutRepository;

    public ExerciseSet toEntity(ExerciseSetCreateDTO dto) {
        Exercise exercise = exerciseRepository.findById(dto.exercise_id())
            .orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + dto.exercise_id()));
        Workout workout = workoutRepository.findById(dto.workout_id())
            .orElseThrow(() -> new IllegalArgumentException("Workout not found: " + dto.workout_id()));

        return switch (dto.type()) {
            case ExerciseSetType.FREE_WEIGHT -> freeWeightSetMapper.fromExerciseSetCreateDTO(dto, exercise, workout);
            // future:
            // case ExerciseSetType.ELASTIC -> elasticMapper.fromDTO(...)
            default -> throw new IllegalArgumentException("Unsupported type: " + dto.type());
        };
    }
}
