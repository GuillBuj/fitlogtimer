package com.fitlogtimer.dto;

import java.time.LocalDate;

public record SetsGroupedFinalForExDTO(
    LocalDate date,
    double bodyWeight,
    String comment,
    Object sets,
    double est1RM) {
    @Override
    public final String toString() {
        return (date + " : " + sets);
    }
}
