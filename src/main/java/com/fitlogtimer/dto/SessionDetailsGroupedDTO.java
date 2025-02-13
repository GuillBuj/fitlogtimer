package com.fitlogtimer.dto;

import java.sql.Date;
import java.util.List;


public record SessionDetailsGroupedDTO(
    int id,
    Date date,
    double bodyWeight,
    String comment,
    List<SetGroupedDTO> exerciseSets
) {
    
}
