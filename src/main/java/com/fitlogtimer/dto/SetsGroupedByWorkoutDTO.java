package com.fitlogtimer.dto;

import java.util.List;

public record SetsGroupedByWorkoutDTO(
    int id,
    String name,
    List<SetsGroupedForExDTO> exerciseSets
) {

}
