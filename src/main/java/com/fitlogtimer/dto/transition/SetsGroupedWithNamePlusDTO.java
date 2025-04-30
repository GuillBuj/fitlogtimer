package com.fitlogtimer.dto.transition;

import java.time.LocalDate;
import java.util.List;

import com.fitlogtimer.dto.base.SetBasicInterfaceDTO;


public record SetsGroupedWithNamePlusDTO(
    String exerciseNameShort,
    LocalDate date,
    double bodyWeight,
    String type,
    List<SetBasicInterfaceDTO> sets
) {
    
}
