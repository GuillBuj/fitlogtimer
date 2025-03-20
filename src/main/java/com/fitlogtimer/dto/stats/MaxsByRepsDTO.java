package com.fitlogtimer.dto.stats;

import java.util.Map;

public record MaxsByRepsDTO(Map<Integer, MaxWeightWith1RMAndDateDTO> maxsByReps) {

}
