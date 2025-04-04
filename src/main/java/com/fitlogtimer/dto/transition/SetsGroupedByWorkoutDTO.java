package com.fitlogtimer.dto.transition;

import java.util.List;

public record SetsGroupedByWorkoutDTO(
    int id,
    String name,
    List<SetsGroupedForExDTO> exerciseSets
) {

}
