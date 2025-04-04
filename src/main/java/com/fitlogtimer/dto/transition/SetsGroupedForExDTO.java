package com.fitlogtimer.dto.transition;

import java.util.List;

import com.fitlogtimer.dto.base.SetBasicWith1RMDTO;

public record SetsGroupedForExDTO(
    int idWorkout,    
    List<SetBasicWith1RMDTO> setGroup) {

}
