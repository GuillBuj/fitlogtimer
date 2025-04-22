package com.fitlogtimer.dto.transition;

import java.util.List;

import com.fitlogtimer.dto.base.SetBasicDTO;
import com.fitlogtimer.dto.base.SetBasicInterfaceDTO;


public record SetsGroupedWithNameDTO(
    String exerciseNameShort,
    List<SetBasicInterfaceDTO> sets
) {
    
}
