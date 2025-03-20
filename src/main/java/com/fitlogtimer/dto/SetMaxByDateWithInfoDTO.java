package com.fitlogtimer.dto;

import java.time.LocalDateTime;

public record SetMaxByDateWithInfoDTO(
    LocalDateTime date, 
    double bodyWeight, 
    SetBasicWith1RMDTO maxSet
) {

}
