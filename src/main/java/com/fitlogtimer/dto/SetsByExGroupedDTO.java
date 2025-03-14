package com.fitlogtimer.dto;

import java.util.List;



public record SetsByExGroupedDTO(
    int id,
    String exercise,
    List<SetsGroupedFinalForExDTO> exerciseSets
) {
    
}
