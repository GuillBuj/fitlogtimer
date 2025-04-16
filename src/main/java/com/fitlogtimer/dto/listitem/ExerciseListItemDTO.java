package com.fitlogtimer.dto.listitem;

import com.fitlogtimer.enums.Family;
import com.fitlogtimer.enums.Muscle;
import com.fitlogtimer.model.Exercise;

public record ExerciseListItemDTO(
    int id,
    String name,
    String shortName,
    Muscle muscle,
    Family family,
    String type,
    double personalBest,
    double oneRepMaxEst
    ) {
        public static ExerciseListItemDTO from(Exercise exercise, double personalBest, double oneRepMaxEst) {
                return new ExerciseListItemDTO(
                    exercise.getId(),
                    exercise.getName(),
                    exercise.getShortName(),
                    exercise.getMuscle(),
                    exercise.getFamily(),
                    exercise.getType(),
                    personalBest,
                    oneRepMaxEst
                );
            }
    }
