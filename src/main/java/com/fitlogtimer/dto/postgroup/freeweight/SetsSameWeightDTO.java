package com.fitlogtimer.dto.postgroup.freeweight;

import java.util.List;

public record SetsSameWeightDTO(
    double weight,
    List<Integer> reps
) {
    @Override
    public final String toString() {
        return (weight!=0 ? reps + " @ " + weight + "kg" : reps.toString());
    }
}
