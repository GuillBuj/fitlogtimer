package com.fitlogtimer.dto;

public record SetInSetsGrouped(
    Long id,
    double weight,
    int repNumber,
    boolean isMax,
    String comment) {
}
