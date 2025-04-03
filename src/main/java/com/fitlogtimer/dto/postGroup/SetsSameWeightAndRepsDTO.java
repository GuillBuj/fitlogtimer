package com.fitlogtimer.dto.postgroup;

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
