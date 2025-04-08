package com.fitlogtimer.dto.display;

import java.time.LocalDate;
import java.util.List;

public record CalendarItemDisplayDTO(
    int id,
    String type,
    LocalDate date,
    String color,
    List<ExerciseDisplayForWorkoutListItem> exercises

) {

}
