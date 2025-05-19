package com.fitlogtimer.dto.postgroup;

import java.util.List;

import com.fitlogtimer.dto.base.SetBasicMovementDTO;

public record SetsAllDifferentMovementDTO(List<SetBasicMovementDTO> sets) {
    
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        for (SetBasicMovementDTO set : sets) {
            if (sb.length() > 0) { sb.append(" , ");}
            double weight = set.weight();
            String weightS = weight > 0 ? "(+" + String.valueOf(weight) + ")" : "";
            String bandsS = "(" + set.bands() + ")";
            sb.append(set.repNumber() + " " + set.distance() + bandsS + weightS);
        }
        return sb.toString();
    }
}
