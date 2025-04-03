package com.fitlogtimer.dto.postgroup;

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
