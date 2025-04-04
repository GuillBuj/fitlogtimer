package com.fitlogtimer.dto.listitem;

import java.time.LocalDate;

public record SetGroupCleanExerciseListItemDTO(
    LocalDate date,
    double bodyWeight,
    String type,
    Object sets,
    double est1RM) {
    @Override
    public final String toString() {
        return (date + " : " + sets);
    }
}
