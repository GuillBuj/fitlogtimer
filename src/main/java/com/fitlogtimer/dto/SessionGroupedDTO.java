package com.fitlogtimer.dto;

import java.sql.Date;
import java.util.List;


public record SessionGroupedDTO(
    int id,
    Date date,
    double bodyWeight,
    String comment,
    List<SetsGroupedFinalDTO> exerciseSets
) {
    
}
