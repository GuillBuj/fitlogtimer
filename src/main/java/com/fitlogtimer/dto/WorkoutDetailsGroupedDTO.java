package com.fitlogtimer.dto;

import java.time.LocalDate;
import java.util.List;


public record WorkoutDetailsGroupedDTO(
    int id,
    LocalDate date,
    double bodyWeight,
    String comment,
    List<SetsGroupedDTO> exerciseSets
) {
    
}
