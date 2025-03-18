package com.fitlogtimer.dto.stats;

import java.util.Map;

public record MaxsByRepsDTO(Map<Integer, MaxWeightWithDateDTO> maxsByReps) {

}
