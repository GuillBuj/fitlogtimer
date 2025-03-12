package com.fitlogtimer.dto;

import java.time.LocalDate;

public record SessionOutDTO(
    int id,
    LocalDate date,
    double bodyWeight,
    String comment){ 
}
