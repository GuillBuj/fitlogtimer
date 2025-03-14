package com.fitlogtimer.dto;

public record SetBasicDTO(
    int repNumber, 
    double weight
) {
    @Override
    public final String toString() {
        return (repNumber + " @ " + weight + "kg");
    }
}
