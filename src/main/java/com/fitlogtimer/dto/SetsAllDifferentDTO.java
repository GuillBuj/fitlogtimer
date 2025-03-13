package com.fitlogtimer.dto;

import java.util.List;

public record SetsAllDifferentDTO(List<SetBasicDTO> sets) {
    
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        for (SetBasicDTO set : sets) {
            if (sb.length() > 0) {
                sb.append(" , ");
            }
            sb.append(set.nbReps())
            .append(" @ ")
            .append(set.weight())
            .append(" kg");
        }
        return sb.toString();
    }
}
