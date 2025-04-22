package com.fitlogtimer.dto.base;

public record SetBasicIsometricDTO(int durationS, double weight) implements SetBasicInterfaceDTO{
    @Override
    public final String toString() {

        return weight == 0?
                String.valueOf(durationS) + "\"" :
                String.valueOf(durationS) + "\" (+" + weight + " kg)";
    }
}
