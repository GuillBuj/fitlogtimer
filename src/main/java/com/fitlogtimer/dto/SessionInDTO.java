package com.fitlogtimer.dto;

import java.sql.Date;

public record SessionInDTO(
    Date date,
    double bodyWeight,
    String comment){ 
}
