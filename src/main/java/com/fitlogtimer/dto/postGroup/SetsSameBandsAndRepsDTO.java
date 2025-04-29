package com.fitlogtimer.dto.postgroup;

public record SetsSameBandsAndRepsDTO(
    int setsNumber,
    int repNumber,
    String bands
) {
    @Override
    public final String toString() {
            return (setsNumber + " * " + repNumber + " (" + bands + ")");
    }
}
