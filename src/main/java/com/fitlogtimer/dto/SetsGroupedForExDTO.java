package com.fitlogtimer.dto;

import java.util.List;

public record SetsGroupedForExDTO(
    int idWorkout,    
    List<SetBasicWith1RMDTO> setGroup) {

}
