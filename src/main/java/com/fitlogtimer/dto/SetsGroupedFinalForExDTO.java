package com.fitlogtimer.dto;

import java.time.LocalDate;

public record SetsGroupedFinalForExDTO(
    LocalDate date,
    double bodyWeight,
    String comment,
    Object sets) {
    @Override
    public final String toString() {
        return (date + " : " + sets);
    }
}
