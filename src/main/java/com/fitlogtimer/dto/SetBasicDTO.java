package com.fitlogtimer.dto;

public record SetBasicDTO(
    int repNumber, 
    double weight
) {
    public SetBasicDTO(SetBasicWith1RMDTO setBasicWith1RMDTO){
        this(setBasicWith1RMDTO.repNumber(), 
             setBasicWith1RMDTO.weight());
    }
    
    @Override
    public final String toString() {
        return (repNumber + " @ " + weight + "kg");
    }
}
