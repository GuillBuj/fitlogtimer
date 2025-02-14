package com.fitlogtimer.dto;

import java.util.List;

public record SetsSameWeightDTO(
    double weight,
    List<Integer> reps
) {
    @Override
    public final String toString() {
        return (reps + " @ " + weight + "kg");
    }
}
