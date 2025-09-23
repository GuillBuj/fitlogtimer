package com.fitlogtimer.dto.fromxlsx;

import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;

import java.time.LocalDate;
import java.util.List;

public record FromJsonWorkoutDTO(
    LocalDate date,
    double bodyWeight,
    List<ExerciseSetCreateDTO> sets,
    String name) {
}
