package com.fitlogtimer.dto;

import java.util.List;


public record SetsGroupedWithNameDTO(
    String exerciseNameShort,
    List<SetBasicDTO> sets
) {
    
}
