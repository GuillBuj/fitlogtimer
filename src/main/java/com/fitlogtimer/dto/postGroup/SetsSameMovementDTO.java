package com.fitlogtimer.dto.postgroup;

public record SetsSameMovementDTO(
    int setsNumber,
    int reps,
    String distance,
    String bands,
    double weight
) {
    @Override
    public final String toString() {
        String weightS = weight > 0 ? " (+" + String.valueOf(weight) + "kg)" : "";
        String bandsS = " (" + bands + ")";  
        return (setsNumber + " * " + reps + " " + distance + bandsS + weightS);
    }
}
