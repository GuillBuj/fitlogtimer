package com.fitlogtimer.dto.postgroup;

import java.util.List;

public record SetsSameRepsElasticDTO(
    int reps,
    List<String> bands
) {
    @Override
    public final String toString() {
        return (reps + " (" + bands + ")");
    }
}
