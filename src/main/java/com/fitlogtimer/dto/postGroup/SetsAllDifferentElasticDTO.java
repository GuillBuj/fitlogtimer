package com.fitlogtimer.dto.postgroup;

import java.util.List;

import com.fitlogtimer.dto.base.SetBasicElasticDTO;

public record SetsAllDifferentElasticDTO(List<SetBasicElasticDTO> sets) {
    
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        for (SetBasicElasticDTO set : sets) {
            if (sb.length() > 0) { sb.append(" , ");}
                sb.append(set.repNumber())
                .append(" (")
                .append(set.bands())
                .append(")"); 
        }
        return sb.toString();
    }
}
