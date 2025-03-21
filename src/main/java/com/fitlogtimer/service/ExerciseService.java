package com.fitlogtimer.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.ExerciseCreateDTO;
import com.fitlogtimer.mapper.ExerciseMapper;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.repository.ExerciseRepository;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Data
public class ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseMapper exerciseMapper;

    @Transactional
    public Exercise createExercise(ExerciseCreateDTO exerciseCreateDTO) {
        
        Exercise exercise = exerciseRepository.save(exerciseMapper.toExercise(exerciseCreateDTO));
        log.info("Exercise created: " + exercise);
        
        return exercise;
    }

    @Transactional
    public boolean deleteExerciseSet(int id){
        
        if (exerciseRepository.existsById(id)) {
            exerciseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Exercise> getAllExercises(){
        return exerciseRepository.findAll();
    };

    public Optional<Exercise> getById(int id){
        return exerciseRepository.findById(id);
    }


}
