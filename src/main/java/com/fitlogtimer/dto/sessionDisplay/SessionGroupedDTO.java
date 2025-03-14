package com.fitlogtimer.dto.sessionDisplay;

import java.time.LocalDate;
import java.util.List;

import com.fitlogtimer.dto.SetsGroupedFinalDTO;


public record SessionGroupedDTO(
    int id,
    LocalDate date,
    double bodyWeight,
    String comment,
    List<SetsGroupedFinalDTO> exerciseSets
) {
    
}
