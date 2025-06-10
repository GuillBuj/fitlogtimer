package com.fitlogtimer.service;

import com.fitlogtimer.repository.WorkoutTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkoutTypeService {
    private final WorkoutTypeRepository workoutTypeRepository;
}
