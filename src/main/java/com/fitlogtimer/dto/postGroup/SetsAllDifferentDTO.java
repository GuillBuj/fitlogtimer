package com.fitlogtimer.dto.postgroup;

import java.util.List;

import com.fitlogtimer.dto.base.SetBasicDTO;

public record SetsAllDifferentDTO(List<SetBasicDTO> sets) {
    
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        for (SetBasicDTO set : sets) {
            if (sb.length() > 0) { sb.append(" , ");}
            sb.append(set.repNumber())
                .append(" @ ")
                .append(set.weight())
                .append(" kg");
        }
        return sb.toString();
    }
}
