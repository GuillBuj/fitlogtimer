package com.fitlogtimer.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.repository.ExerciseRepository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Data
public class ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    public Exercise saveExercise(Exercise exercise) {
        exerciseRepository.save(exercise);
        log.info("Exercise saved: " + exercise);
        return exercise;
    }

    public List<Exercise> getAllExercises(){
        return exerciseRepository.findAll();
    };

    public Optional<Exercise> getById(int id){
        return exerciseRepository.findById(id);
    }
}
