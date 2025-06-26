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
    String color,
    double personalBest,
    double oneRepMaxEst,
    double seasonBest,
    double seasonOneRepMaxEst
    ) {
        public static ExerciseListItemDTO from(Exercise exercise, double personalBest, double oneRepMaxEst, double personalSeasonBest, double seasonOneRepMaxEst) {
                return new ExerciseListItemDTO(
                    exercise.getId(),
                    exercise.getName(),
                    exercise.getShortName(),
                    exercise.getMuscle(),
                    exercise.getFamily(),
                    exercise.getType(),
                    exercise.getColor(),
                    personalBest,
                    oneRepMaxEst,
                    personalSeasonBest,
                    seasonOneRepMaxEst
                );
            }
    }
