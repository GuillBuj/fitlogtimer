package com.fitlogtimer.dto;

import java.sql.Date;

public record SessionOutDTO(
    int id,
    Date date,
    double bodyWeight,
    String comment){ 
}
