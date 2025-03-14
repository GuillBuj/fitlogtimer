package com.fitlogtimer.dto;

import java.util.List;

public record SetsGroupedBySessionDTO(
    int id,
    String name,
    List<SetsGroupedForExDTO> exerciseSets
) {

}
