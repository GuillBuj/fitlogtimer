package com.fitlogtimer.dto;

import java.sql.Date;
import java.util.List;


public record SetsGroupedWithNameDTO(
    int exercise_id,
    List<SetInSetsGrouped> sets
) {
    
}
