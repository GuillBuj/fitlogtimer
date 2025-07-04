package com.fitlogtimer.service;

import com.fitlogtimer.repository.ExerciseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SettingsService {
    private final ExerciseRepository exerciseRepository;


}
