package com.fitlogtimer.dto.postGroup;

public record SetsSameWeightAndRepsDTO(
    int setsNumber,
    int repNumber,
    double weight
) {
    @Override
    public final String toString() {
        return (setsNumber + " * " + repNumber + " @ " + weight + "kg");
    }
}
