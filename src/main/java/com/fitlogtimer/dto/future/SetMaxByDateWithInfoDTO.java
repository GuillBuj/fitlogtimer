package com.fitlogtimer.dto.future;

import java.time.LocalDateTime;

import com.fitlogtimer.dto.base.SetBasicWith1RMDTO;

public record SetMaxByDateWithInfoDTO(
    LocalDateTime date, 
    double bodyWeight, 
    SetBasicWith1RMDTO maxSet
) {

}
