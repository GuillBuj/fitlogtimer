package com.fitlogtimer.dto;

public record LastSetDTO(
    int exerciseId,
    String exerciseName,
    int nbReps, 
    double weight) {

}
