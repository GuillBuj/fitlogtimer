package com.fitlogtimer.dto.base;

public record SetBasicMovementDTO(
    int repNumber,
    String distance,
    String bands,
    double weight
) implements SetBasicInterfaceDTO {
    @Override
    public final String toString() {
        String weightS = weight > 0 ? "( +" + String.valueOf(weight) + " )" : "";
        String bandsS = "( " + bands + " )";
        return repNumber + distance + bandsS + weightS;
    }
}
