package com.fitlogtimer.service;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fitlogtimer.dto.update.ExerciseUpdateDTO;
import com.fitlogtimer.model.preference.ExerciseListPreference;
import com.fitlogtimer.model.preference.ExercisePreference;
import org.springframework.stereotype.Service;

import com.fitlogtimer.constants.ExerciseSetType;
import com.fitlogtimer.dto.create.ExerciseCreateDTO;
import com.fitlogtimer.dto.listitem.ExerciseListItemDTO;
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

    private final ExercisePreferenceService exercisePreferenceService;

    @Transactional
    public Exercise createExercise(ExerciseCreateDTO exerciseCreateDTO) {
        log.info("exerciseCreateDTO: {}",exerciseCreateDTO.toString());
        Exercise exercise = exerciseRepository.save(exerciseMapper.toEntity(exerciseCreateDTO));
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
        double personalBest = 0;
        double oneRepMaxEst = 0;
        double seasonBest = 0;
        double seasonOneRepMax = 0;
        
        switch (exercise.getType()) {
            case ExerciseSetType.FREE_WEIGHT:
                personalBest = statsService.getPersonalBest(id);
                oneRepMaxEst = statsService.getBest1RMest(id);
                seasonBest = statsService.getSeasonBest(id);
                seasonOneRepMax = statsService.getSeasonBest1RMest(id);
                break;
            case ExerciseSetType.ISOMETRIC:
                personalBest = statsService.getPersonalBestDuration(id);
                break;
            case ExerciseSetType.BODYWEIGHT:
                personalBest = statsService.getPersonalBestZero(id);
                break;
        }
         
        return ExerciseListItemDTO.from(exercise, personalBest, oneRepMaxEst, seasonBest, seasonOneRepMax);
    }

    public List<ExerciseListItemDTO> getAllExerciseItems() {
        List<Exercise> exercises = exerciseRepository.findAll();
        return exercises.stream()
                        .map(exercise -> {
                            return getExerciseListItem(exercise.getId());
                        })
                        .collect(Collectors.toList());
    }

    public List<ExerciseListItemDTO> getDefaultExerciseItems() throws IOException {
        ExerciseListPreference defaultList = exercisePreferenceService.getListByName("default");
        if (defaultList == null) return List.of();

        // On prépare une Map des entités Exercise pour accès rapide par ID
        Map<Integer, Exercise> exerciseMap = exerciseRepository.findAll().stream()
                .collect(Collectors.toMap(Exercise::getId, Function.identity()));

        // Filtrer et trier les exercices visibles
        return defaultList.getExercises().stream()
                .filter(ExercisePreference::isVisible)  // Filtrer les exercices visibles
                .sorted(Comparator.comparingInt(ExercisePreference::getOrder))  // Trier par ordre
                .map(pref -> {
                    Exercise ex = exerciseMap.get(pref.getExerciseId());
                    return ex != null ? getExerciseListItem(ex.getId()) : null;
                })
                .filter(Objects::nonNull)  // Exclure les exercices nuls
                .collect(Collectors.toList());
    }

    public ExerciseListItemDTO getExerciseListItemDTO(Exercise exercise, Double personalBest, Double oneRepMaxEst, Double seasonBest, Double seasonOneRepMax) {
        return  ExerciseListItemDTO.from(
                exercise,
                personalBest != null ? personalBest : 0.0,
                oneRepMaxEst != null ? oneRepMaxEst : 0.0,
                seasonBest != null ? seasonBest : 0.0,
                seasonOneRepMax != null ? seasonOneRepMax : 0.0);
    }

    public Exercise findById(int id) {
        return exerciseRepository.findById(id).orElseThrow(() -> new NotFoundException("Exercise not found"));
    }

//    public Exercise updateExercise(ExerciseUpdateDTO exerciseUpdateDTO) {
//        return exerciseRepository.save(exerciseMapper.toEntity(exerciseUpdateDTO));
//    }

    public Exercise updateExercise(ExerciseListItemDTO exerciseListItemDTO){
        return exerciseRepository.save(exerciseMapper.toEntity(exerciseListItemDTO));
    }

    public ExerciseUpdateDTO getExerciseUpdateDTO(int id) {
        return exerciseMapper.toExerciseUpdateDTO(exerciseRepository.getById(id));
    }

    public Map<String, String> getAllExerciseColors(){
        return exerciseRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Exercise::getShortName,
                        exercise -> exercise.getColor() != null ? exercise.getColor() : "#666666"));
    }

}
