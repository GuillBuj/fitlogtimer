package com.fitlogtimer.dto.display;

import java.time.LocalDate;
import java.util.List;

public record CalendarItemDisplayDTO(
    int idWorkout,
    WorkoutTypeDisplayDTO type,
    LocalDate date,
    List<ExerciseDisplayDTO> exercises

) {

}
