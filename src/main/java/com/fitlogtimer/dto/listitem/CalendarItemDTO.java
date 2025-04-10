package com.fitlogtimer.dto.listitem;

import java.time.LocalDate;
import java.util.List;

public record CalendarItemDTO(
    int idWorkout,
    String workoutType,
    LocalDate date,
    List<String> exerciseShortNames
) {

}
