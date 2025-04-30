package com.fitlogtimer.dto.listitem;

import java.time.LocalDate;

import jakarta.annotation.Nullable;

public record SetGroupCleanExerciseListItemDTO(
    LocalDate date,
    double bodyWeight,
    String type,
    Object sets,
    @Nullable Double est1RM) {
    @Override
    public final String toString() {
        return (date + " : " + sets);
    }
}
