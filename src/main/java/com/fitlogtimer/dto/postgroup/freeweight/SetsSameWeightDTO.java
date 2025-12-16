package com.fitlogtimer.dto.postgroup.freeweight;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

public record SetsSameWeightDTO(
    double weight,
    List<Integer> reps
) {
    @Override
    public final String toString() {

        String weightStr = String.valueOf(weight);
        if (weightStr.endsWith(".0")) {
            weightStr = weightStr.substring(0, weightStr.length() - 2);
        }

        return weight!=0 ? reps + " @ " + weightStr + "kg" : reps.toString();
    }
}
