package com.fitlogtimer.dto.fromxlsx;

import java.time.LocalDate;
import java.util.List;

import com.fitlogtimer.dto.base.SetBasicWithExDTO;

public record FromXlsxDCVarDTO(
    LocalDate date,
    double bodyWeight,
    List<SetBasicWithExDTO> sets) {

}
