package com.fitlogtimer.dto.postgroup;

import java.util.List;

public record SetsSameBandsDTO(
    String bands,
    List<Integer> reps
) {
    @Override
    public final String toString() {
        return reps + " (" + bands + ")";
    }
}
