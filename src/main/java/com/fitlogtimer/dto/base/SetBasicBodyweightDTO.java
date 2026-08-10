package com.fitlogtimer.dto.base;

import com.fitlogtimer.enums.SetMode;

public record SetBasicBodyweightDTO(
        int repNumber,
        Integer durationS,
        double weight,
        SetMode setMode) implements SetBasicInterfaceDTO{
    
    @Override
    public final String toString() {
        if (setMode == SetMode.TIME_TRIAL) {
            int minutes = durationS / 60;
            int seconds = durationS % 60;

            return "TT " + repNumber + " : " + minutes + "'" + String.format("%02d", seconds) + "\"";
        }

        String reps = repNumber == 1 ? "" : String.valueOf(repNumber);
        return weight == 0
                ? reps
                : reps + "(+" + weight + " kg)";
    }
}
