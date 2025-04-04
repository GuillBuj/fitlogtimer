package com.fitlogtimer.dto.fromxlsx;

import java.time.LocalDate;

import com.fitlogtimer.dto.base.SetBasicDTO;

public record FromXlsxDeadliftDTO(
    LocalDate date,
    double bodyWeight,
    SetBasicDTO sets
) {

}