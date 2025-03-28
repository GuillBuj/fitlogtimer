package com.fitlogtimer.dto;

import java.time.LocalDate;
import java.util.List;

public record FromXlsxDCHeavyDTO(
    LocalDate date,
    double bodyWeight,
    List<SetBasicDTO> sets,
    String type
) {

}