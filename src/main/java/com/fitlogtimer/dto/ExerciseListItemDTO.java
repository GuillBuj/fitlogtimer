package com.fitlogtimer.dto;

import com.fitlogtimer.enums.Family;
import com.fitlogtimer.enums.Muscle;

public record ExerciseListItemDTO(
    int id,
    String name,
    String shortName,
    Muscle muscle,
    Family family,
    double personalBest) {

}
