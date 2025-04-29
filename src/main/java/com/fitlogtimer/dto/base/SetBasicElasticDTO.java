package com.fitlogtimer.dto.base;

public record SetBasicElasticDTO(
    int repNumber, 
    String bands
) implements SetBasicInterfaceDTO {
    @Override
    public final String toString() {
        return (repNumber + " ( " + bands + " )");
    }
}
