package com.fitlogtimer.dto.postgroup;

import java.util.List;

import com.fitlogtimer.dto.base.SetBasicIsometricDTO;

public record SetsAllDifferentIsometricDTO(List<SetBasicIsometricDTO> sets) {
    
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        for (SetBasicIsometricDTO set : sets) {
            if (sb.length() > 0) { sb.append(" , ");}
            String reps = set.repNumber() == 1 ? "" : set.repNumber() + " * ";
            String weight = set.weight() == 0 ? "" : " @ " + set.weight() + " kg";
            sb.append(reps)
                .append(set.durationS())
                .append("\"")
                .append(weight);
        }
        return sb.toString();
    }
}
