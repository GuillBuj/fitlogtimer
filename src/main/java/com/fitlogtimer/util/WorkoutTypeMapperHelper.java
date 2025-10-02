package com.fitlogtimer.util;

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
        return workoutTypeRepository.findByName(name)
                .orElseGet(() -> {
                    WorkoutType newType = new WorkoutType();
                    newType.setName(name);
                    return workoutTypeRepository.save(newType);
                });
    }
}
