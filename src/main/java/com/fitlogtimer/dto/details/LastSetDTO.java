package com.fitlogtimer.dto.details;

public record LastSetDTO(
    int exerciseId,
    String exerciseName,
    int repNumber, 
    double weight,
    String bands,
    int durationS,
    String distance) {

}
