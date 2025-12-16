package com.fitlogtimer.dto.postgroup.freeweight;

public record SetsSameWeightAndRepsDTO(
    int setsNumber,
    int repNumber,
    double weight
) {
    @Override
    public final String toString() {
        String setStr = setsNumber == 1 ? "" : setsNumber + " * ";
        String weightStr = " @ " + String.valueOf(weight);
        if (weightStr.endsWith(".0")) {
            weightStr = weightStr.substring(0, weightStr.length() - 2);
        };

        
        return setStr + repNumber + weightStr + "kg";
    }
}
