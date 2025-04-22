package com.fitlogtimer.dto.postgroup;

public record SetsSameWeightAndRepsDTO(
    int setsNumber,
    int repNumber,
    double weight
) {
    @Override
    public final String toString() {
        if (weight == 0) {
            return setsNumber + " * " + repNumber;
        } else {
            return (setsNumber + " * " + repNumber + " @ " + weight + "kg");
        }
    }
}
