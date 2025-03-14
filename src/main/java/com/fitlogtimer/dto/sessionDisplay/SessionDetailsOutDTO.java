package com.fitlogtimer.dto.sessionDisplay;

import java.time.LocalDate;
import java.util.List;

import com.fitlogtimer.dto.SetInSessionOutDTO;


public record SessionDetailsOutDTO(
    int id,
    LocalDate date,
    double bodyWeight,
    String comment,
    List<SetInSessionOutDTO> exerciseSets
) {
    
}
