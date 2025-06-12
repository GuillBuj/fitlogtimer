package com.fitlogtimer.util.mapperhelper;

import com.fitlogtimer.model.WorkoutType;
import com.fitlogtimer.repository.WorkoutTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class WorkoutTypeMapperHelper {
    private WorkoutTypeRepository workoutTypeRepository;

    public String map(WorkoutType workoutType) {
        return workoutType != null ? workoutType.getName() : null;
    }

    public WorkoutType map(String name) {
        return workoutTypeRepository.findByName(name).orElseThrow(() -> new IllegalArgumentException("Invalid WorkoutType: " + name));
    }
}
