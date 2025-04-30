package com.fitlogtimer.dto.transition;

import java.util.List;

import com.fitlogtimer.dto.base.SetBasicInterfaceDTO;

public record SetsGroupedForExDTO(
    int idWorkout,    
    List<? extends SetBasicInterfaceDTO> setGroup) {
}
