package com.fitlogtimer.dto.fromxlsx;

import java.time.LocalDate;
import java.util.List;

import com.fitlogtimer.dto.base.SetBasicWithExDTO;

public record FromXlsxDCLightDTO(
    LocalDate date,
    double bodyWeight,
    List<SetBasicWithExDTO> sets,
    String type) {

}
