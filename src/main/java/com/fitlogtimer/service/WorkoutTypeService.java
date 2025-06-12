package com.fitlogtimer.service;

import com.fitlogtimer.model.WorkoutType;
import com.fitlogtimer.repository.WorkoutTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutTypeService {
    private final WorkoutTypeRepository workoutTypeRepository;

    public WorkoutType findOrCreate(String name){

        if(name == null || name.isBlank()){
            log.error("WorkoutType name is null or empty");
            return null;
        }

        return workoutTypeRepository.findByName(name)
                .orElseGet(()-> {
                    log.info("Creating new workout type {}", name);
                    WorkoutType newType = new WorkoutType(name);
                    return workoutTypeRepository.save(newType);
                });
    }
}
