package com.fitlogtimer.dto.listitem;

import com.fitlogtimer.enums.SetMode;

public record SetGroupCleanWorkoutListItemDTO(
        String exerciseNameShort,
        Object sets,
        SetMode setMode) {
    @Override
    public final String toString() {
        return (exerciseNameShort + " : " + sets + "(" + setMode +")");
    }
}
