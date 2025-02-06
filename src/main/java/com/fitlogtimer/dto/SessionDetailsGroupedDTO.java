package com.fitlogtimer.dto;

import java.sql.Date;
import java.util.List;


public record SessionDetailsGroupedDTO(
    Long id,
    Date date,
    double bodyWeight,
    String comment,
    List<List<ExerciseSetInSessionDTO>> exerciseSets
) {
    
}
