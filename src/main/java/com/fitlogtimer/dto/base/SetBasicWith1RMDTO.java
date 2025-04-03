package com.fitlogtimer.dto.base;

import com.fitlogtimer.service.StatsService;

public record SetBasicWith1RMDTO(
    int repNumber, 
    double weight,
    double oneRepMax
) implements Comparable<SetBasicWith1RMDTO>{
    
    public SetBasicWith1RMDTO(SetBasicDTO setBasicDTO){
        this(setBasicDTO.repNumber(), 
             setBasicDTO.weight(), 
             StatsService.calculateOneRepMax(setBasicDTO.repNumber(), setBasicDTO.weight()));
    }

    SetBasicWith1RMDTO(int repNumber, double weight){
        this(repNumber, 
             weight, 
             StatsService.calculateOneRepMax(repNumber, weight));
    }

    @Override
    public int compareTo(SetBasicWith1RMDTO other) {
        return Double.compare(this.oneRepMax(), other.oneRepMax());
    }
}
