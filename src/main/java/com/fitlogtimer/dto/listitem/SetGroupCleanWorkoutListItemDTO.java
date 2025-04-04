package com.fitlogtimer.dto.listitem;

public record SetGroupCleanWorkoutListItemDTO(
    String exerciseNameShort,
    Object sets) {
    @Override
    public final String toString() {
        return (exerciseNameShort + " : " + sets);
    }
}
