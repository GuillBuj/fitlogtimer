package com.fitlogtimer.dto.transition;

import java.util.List;

import com.fitlogtimer.dto.base.SetBasicInterfaceDTO;
import com.fitlogtimer.enums.SetMode;

public record SetsGroupedForExDTO(
    int idWorkout,
    SetMode setMode,
    List<? extends SetBasicInterfaceDTO> setGroup) {
}
