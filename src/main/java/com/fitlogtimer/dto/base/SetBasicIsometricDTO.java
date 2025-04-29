package com.fitlogtimer.dto.base;

public record SetBasicIsometricDTO(
    int durationS,
    int repNumber,
    double weight) implements SetBasicInterfaceDTO{
    
    @Override
    public final String toString() {

        String reps = repNumber == 1? "" : String.valueOf(repNumber);
        
        return weight == 0?
                reps + String.valueOf(durationS) + "\"" :
                reps + String.valueOf(durationS) + "\" (+" + weight + " kg)";
    }
}
