package com.fitlogtimer.dto.transition;

import java.util.List;

import com.fitlogtimer.dto.base.SetBasicDTO;


public record SetsGroupedWithNameDTO(
    String exerciseNameShort,
    List<SetBasicDTO> sets
) {
    
}
