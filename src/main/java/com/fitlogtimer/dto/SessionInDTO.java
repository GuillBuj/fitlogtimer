package com.fitlogtimer.dto;

import java.time.LocalDate;

public record SessionInDTO(
    LocalDate date,
    double bodyWeight,
    String comment){ 
}
