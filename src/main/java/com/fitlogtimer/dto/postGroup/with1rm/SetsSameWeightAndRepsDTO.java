package com.fitlogtimer.dto.postgroup.with1rm;

public record SetsSameWeightAndRepsDTO(
    int setsNumber,
    int repNumber,
    double weight,
    double est1RM
) {
    @Override
    public final String toString() {
        String sets = setsNumber == 1 ? "" : setsNumber + " * ";
        String weights = weight == 0 ? "" : " @ " + weight + "kg";
        String est1RMs = "(" + est1RM + ")";
        
        return sets + repNumber + weights +est1RMs;  
    }
}
