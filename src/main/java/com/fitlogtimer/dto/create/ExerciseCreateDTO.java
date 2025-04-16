package com.fitlogtimer.dto.create;

import com.fitlogtimer.enums.Family;
import com.fitlogtimer.enums.Muscle;

public record ExerciseCreateDTO(
    String name,
    String shortName,
    Muscle muscle,
    Family family,
    String type) {

}
