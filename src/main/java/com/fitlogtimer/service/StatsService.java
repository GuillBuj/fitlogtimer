package com.fitlogtimer.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.stats.MaxWeightWith1RMAndDateDTO;
import com.fitlogtimer.dto.stats.MaxWeightWithDateDTO;
import com.fitlogtimer.dto.stats.MaxsByRepsDTO;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.sets.FreeWeightSet;
import com.fitlogtimer.repository.ExerciseSetRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Slf4j
public class StatsService {

    private final ExerciseSetRepository exerciseSetRepository;

    @FunctionalInterface
    private interface MaxWeightFetcher {
        MaxWeightWithDateDTO fetch(int repNumber);
    }

    public MaxWeightWithDateDTO maxByExAndReps(int exerciseId, int nbReps) {
        List<ExerciseSet> exerciseSets = exerciseSetRepository.findByExerciseIdOrderByWorkoutDateAndIdDesc(exerciseId);
    
        return exerciseSets.stream()
            .filter(set -> set instanceof FreeWeightSet) // on ne considère que les FreeWeight
            .map(set -> (FreeWeightSet) set)
            .filter(set -> set.getRepNumber() >= nbReps)
            .max(Comparator.comparingDouble(FreeWeightSet::getWeight))
            .map(set -> new MaxWeightWithDateDTO(set.getWeight(), set.getWorkout().getDate()))
            .orElse(new MaxWeightWithDateDTO(0.0, null));
    }

    public MaxWeightWithDateDTO maxByExAndRepsForYear(int exerciseId, int repNumber, int year) {
        List<MaxWeightWithDateDTO> results = exerciseSetRepository
                .findMaxWeightByExerciseIdAndRepsAndYear(exerciseId, repNumber, year);

        return results.isEmpty()
                ? new MaxWeightWithDateDTO(0.0, null)
                : results.get(0); // la plus lourde
    }

    private MaxsByRepsDTO mapMaxWeightsByRepsGeneric(List<Integer> repNumbers, MaxWeightFetcher fetcher) {
        Map<Integer, MaxWeightWith1RMAndDateDTO> result = new HashMap<>();

        for (int nbReps : repNumbers) {
            MaxWeightWithDateDTO maxDTO = fetcher.fetch(nbReps);
            if (maxDTO == null || maxDTO.date() == null) continue;

            double weight = maxDTO.maxWeight();
            MaxWeightWith1RMAndDateDTO with1RM = new MaxWeightWith1RMAndDateDTO(
                    weight,
                    calculateOneRepMax(nbReps, weight),
                    maxDTO.date()
            );
            result.put(nbReps, with1RM);
        }

        return new MaxsByRepsDTO(result);
    }

    public MaxsByRepsDTO mapMaxWeightsByReps(int exerciseId, List<Integer> repNumbers) {
        return mapMaxWeightsByRepsGeneric(repNumbers,
                rep -> maxByExAndReps(exerciseId, rep));
    }

    public MaxsByRepsDTO mapMaxWeightsByRepsForYear(int exerciseId, List<Integer> repNumbers, int year) {
        return mapMaxWeightsByRepsGeneric(repNumbers,
                rep -> maxByExAndRepsForYear(exerciseId, rep, year));
    }

    public double getPersonalBest(int exerciseId){
        Double personalBest = exerciseSetRepository.findMaxWeightByExerciseId(exerciseId);
        return personalBest != null ? personalBest : 0.0;
    }

    public double getSeasonBest(int exerciseId){
        Double personalBest = exerciseSetRepository.findMaxWeightByExerciseIdAndYear(exerciseId, LocalDate.now().getYear());
        return personalBest != null ? personalBest : 0.0;
    }

    public double getPersonalBestDuration(int exerciseId){
        Integer maxDuration = exerciseSetRepository.findMaxDurationByExerciseId(exerciseId);
        return (maxDuration != null) ? maxDuration.doubleValue() : 0.0;
    }

    public double getPersonalBestZero(int exerciseId){
        return Optional.ofNullable(exerciseSetRepository.findMaxByExerciseId(exerciseId))
                   .map(Integer::doubleValue)
                   .orElse(0.0);
    }

    public double getBest1RMest(int exerciseId){
        
        List<Integer> listRepNumbers = IntStream.rangeClosed(1, 15)
                                     .boxed()  // Convertir les int en Integer
                                     .collect(Collectors.toList());

        MaxsByRepsDTO mapMaxsByReps = mapMaxWeightsByReps(exerciseId, listRepNumbers);

        Map<Integer, MaxWeightWith1RMAndDateDTO> maxsByReps = mapMaxsByReps.maxsByReps();

        return maxsByReps.values().stream()
            .mapToDouble(MaxWeightWith1RMAndDateDTO::RMest)
            .max()
            .orElse(0.0);
    }

    public double getSeasonBest1RMest(int exerciseId){
        return getBest1RMestByYear(exerciseId, LocalDate.now().getYear());
    }

    public double getBest1RMestByYear(int exerciseId, int year) {
        List<Integer> listRepNumbers = IntStream.rangeClosed(1, 15)
                .boxed()
                .collect(Collectors.toList());

        MaxsByRepsDTO mapMaxsByReps = mapMaxWeightsByRepsForYear(exerciseId, listRepNumbers, year);

        Map<Integer, MaxWeightWith1RMAndDateDTO> maxsByReps = mapMaxsByReps.maxsByReps();

        return maxsByReps.values().stream()
                .mapToDouble(MaxWeightWith1RMAndDateDTO::RMest)
                .max()
                .orElse(0.0);
    }

    //1RMest d'après un mix de 3 formules trouvées sur le net
    public static double calculateOneRepMax(int repNumber, double weight){
        
        double oneRepMax1 = weight * Math.pow(repNumber, 0.1);

        double oneRepMax;

        if(repNumber<10){
            oneRepMax = ((weight / (1.0278 - (0.0278 * repNumber))) + oneRepMax1) / 2;
        } else {
            oneRepMax = ((weight * (1 + (0.0333 * repNumber))) + oneRepMax1) / 2;
        }

        return Math.round(oneRepMax * 100) / 100.0;
    }
}
