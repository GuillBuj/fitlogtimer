package com.fitlogtimer.dto.base;

public record SetBasicWithExDTO(
    int repNumber, 
    double weight,
    String exercise
) {
    @Override
    public final String toString() {
        return (exercise + ": " + repNumber + " @ " + weight + "kg");
    }
}
