package com.fitlogtimer.dto.update;

import com.fitlogtimer.enums.Family;
import com.fitlogtimer.enums.Muscle;

public record ExerciseUpdateDTO(
        int id,
        String name,
        String shortName,
        Muscle muscle,
        Family family,
        String type) {

}
