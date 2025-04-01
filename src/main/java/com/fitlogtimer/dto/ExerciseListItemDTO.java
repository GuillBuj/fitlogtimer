package com.fitlogtimer.dto;

import com.fitlogtimer.enums.Family;
import com.fitlogtimer.enums.Muscle;
import com.fitlogtimer.model.Exercise;

public record ExerciseListItemDTO(
    int id,
    String name,
    String shortName,
    Muscle muscle,
    Family family,
    double personalBest) {
        public static ExerciseListItemDTO from(Exercise exercise, double personalBest) {
                return new ExerciseListItemDTO(
                    exercise.getId(),
                    exercise.getName(),
                    exercise.getShortName(),
                    exercise.getMuscle(),
                    exercise.getFamily(),
                    personalBest
                );
            }
    }
