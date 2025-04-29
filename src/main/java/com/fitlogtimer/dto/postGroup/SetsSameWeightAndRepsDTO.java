package com.fitlogtimer.dto.postgroup;

public record SetsSameWeightAndRepsDTO(
    int setsNumber,
    int repNumber,
    double weight
) {
    @Override
    public final String toString() {
        String sets = setsNumber == 1 ? "" : setsNumber + " * ";
        String weights = weight == 0 ? "" : " @ " + weight + "kg";
        
        return sets + repNumber + weights;  
    }
}
