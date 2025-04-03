package com.fitlogtimer.dto.base;

public record SetBasicDTO(
    int repNumber, 
    double weight
) {
    @Override
    public final String toString() {
        return (repNumber + " @ " + weight + "kg");
    }
}
