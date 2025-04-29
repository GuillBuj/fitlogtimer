package com.fitlogtimer.dto.postgroup;

public record SetsSameDurationRepsAndWeightDTO(
    int setsNumber,
    int durationS,
    int repNumber,    
    double weight
) {
    @Override
    public final String toString() {

        String sets = setsNumber == 1 ? "" : String.valueOf(setsNumber)+ " * ";
        String reps = repNumber == 1 ? "" : String.valueOf(repNumber) + " * ";
        
        return weight == 0?
                sets + reps + String.valueOf(durationS) + "\"" :
                sets + reps + String.valueOf(durationS) + "\" (+" + weight + " kg)";
    }
}
