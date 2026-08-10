package com.fitlogtimer.dto.transition;

import java.util.List;

import com.fitlogtimer.dto.base.SetBasicInterfaceDTO;
import com.fitlogtimer.enums.SetMode;


public record SetsGroupedWithNameDTO(
    String exerciseNameShort,
    List<SetBasicInterfaceDTO> sets,
    SetMode setMode
) {
    
}
