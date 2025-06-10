package com.fitlogtimer.dto.display;

import java.time.LocalDate;
import java.util.List;

public record WorkoutListDisplayDTO(
    int id,
    LocalDate date,
    double bodyWeight,
    String type,
    String comment,
    List<String> exercises){

}
