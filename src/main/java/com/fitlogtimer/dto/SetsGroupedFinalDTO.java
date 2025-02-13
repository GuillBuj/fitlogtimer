package com.fitlogtimer.dto;

public record SetsGroupedFinalDTO(
    String exerciseNameShort,
    Object sets) {
    @Override
    public final String toString() {
        return (exerciseNameShort + " : " + sets);
    }
}
