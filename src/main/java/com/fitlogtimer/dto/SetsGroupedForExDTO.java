package com.fitlogtimer.dto;

import java.util.List;

public record SetsGroupedForExDTO(
    int idSession,    
    List<SetBasicWith1RMDTO> setGroup) {

}
