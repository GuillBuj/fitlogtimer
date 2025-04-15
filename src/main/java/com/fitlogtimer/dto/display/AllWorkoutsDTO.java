package com.fitlogtimer.dto.display;

import java.util.List;
import java.util.Map;

import com.fitlogtimer.dto.listitem.WorkoutListItemDTO;

public record AllWorkoutsDTO(List<WorkoutListItemDTO> workouts,
Map<String, String> exerciseColors,
Map<String, String> workoutTypeColors) {
      
}
