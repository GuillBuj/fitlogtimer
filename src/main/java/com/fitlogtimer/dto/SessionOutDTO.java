package com.fitlogtimer.dto;

import java.sql.Date;

public record SessionOutDTO(
    long id,
    Date date,
    double bodyWeight,
    String comment){ 
}
