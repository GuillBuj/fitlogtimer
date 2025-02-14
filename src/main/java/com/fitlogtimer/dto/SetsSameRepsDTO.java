package com.fitlogtimer.dto;

import java.util.List;

public record SetsSameRepsDTO(
    int reps,
    List<Double> weight
) {
    @Override
    public final String toString() {
        return (reps + " @ " + weight + "kg");
    }
}
