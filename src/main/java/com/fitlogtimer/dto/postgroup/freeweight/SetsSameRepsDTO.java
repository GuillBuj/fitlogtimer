package com.fitlogtimer.dto.postgroup.freeweight;

import java.util.List;

public record SetsSameRepsDTO(
    int reps,
    List<Double> weight
) {
    @Override
    public final String toString() {
        List<String> formattedWeights = weight.stream()
                .map(w -> String.valueOf(w).replaceAll("\\.0$", ""))
                .toList();

        return reps + " @ " + formattedWeights + "kg";
    }
}
