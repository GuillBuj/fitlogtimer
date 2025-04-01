package com.fitlogtimer.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.ExerciseCreateDTO;
import com.fitlogtimer.dto.ExerciseListItemDTO;
import com.fitlogtimer.exception.NotFoundException;
import com.fitlogtimer.mapper.ExerciseMapper;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.repository.ExerciseRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    private final ExerciseMapper exerciseMapper;

    private final StatsService statsService;

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

    public ExerciseListItemDTO getExerciseListItem(int id) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(() -> new NotFoundException("Exercise not found"));
        double personalBest = statsService.getPersonalBest(id);
        
        return exerciseMapper.toExerciseListItem(exercise, personalBest);
    }

    public List<ExerciseListItemDTO> getAllExerciseItems() {
        List<Exercise> exercises = exerciseRepository.findAll();

        return exercises.stream()
                        .map(exercise -> {
                            return getExerciseListItem(exercise.getId());
                        })
                        .collect(Collectors.toList());
    }
}
