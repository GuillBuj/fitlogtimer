package com.fitlogtimer.dto.fromxlsx;

import java.time.LocalDate;
import java.util.List;

import com.fitlogtimer.dto.base.SetBasicDTO;

public record FromXlsxDCHeavyDTO(
    LocalDate date,
    double bodyWeight,
    List<SetBasicDTO> sets,
    String type
) {

}