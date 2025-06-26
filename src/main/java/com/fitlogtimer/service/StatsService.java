package com.fitlogtimer.service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fitlogtimer.dto.stats.CombinedMaxDTO;
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

//    public MaxsByRepsDTO mapFilteredMaxWeightsByReps(int exerciseId) {
//        Map<Double, Map.Entry<Integer, MaxWeightWith1RMAndDateDTO>> weightToBestEntry = new HashMap<>();
//
//        for (int nbReps = 1; nbReps <= 30; nbReps++) {
//            MaxWeightWithDateDTO maxWeightWithDateDTO = maxByExAndReps(exerciseId, nbReps);
//            //MaxWeightWithDateDTO maxWeightWithDateDTO = maxByExAndRepsForYear(exerciseId, nbReps, 2025);
//            double weight = maxWeightWithDateDTO.maxWeight();
//            if (weight == 0) continue; // ignore les entrées sans données
//            MaxWeightWith1RMAndDateDTO enriched = new MaxWeightWith1RMAndDateDTO(
//                    weight,
//                    calculateOneRepMax(nbReps, weight),
//                    maxWeightWithDateDTO.date()
//            );
//
//            if (!weightToBestEntry.containsKey(weight) || nbReps > weightToBestEntry.get(weight).getKey()) {
//                weightToBestEntry.put(weight, Map.entry(nbReps, enriched));
//            }
//        }
//
//        Map<Integer, MaxWeightWith1RMAndDateDTO> filtered = new TreeMap<>();
//        for (Map.Entry<Integer, MaxWeightWith1RMAndDateDTO> entry : weightToBestEntry.values()) {
//            filtered.put(entry.getKey(), entry.getValue());
//        }
//
//        return new MaxsByRepsDTO(filtered);
//    }

    public List<CombinedMaxDTO> mergeMaxsByReps(
            Map<Integer, MaxWeightWith1RMAndDateDTO> personalBests,
            Map<Integer, MaxWeightWith1RMAndDateDTO> seasonBests
    ) {
        Set<Integer> allReps = new TreeSet<>();
        allReps.addAll(personalBests.keySet());
        allReps.addAll(seasonBests.keySet());

        List<CombinedMaxDTO> combined = new ArrayList<>();
        for (Integer reps : allReps) {
            MaxWeightWith1RMAndDateDTO personalBest = personalBests.get(reps);
            MaxWeightWith1RMAndDateDTO seasonBest = seasonBests.get(reps);
            combined.add(new CombinedMaxDTO(reps, personalBest, seasonBest));
        }

        return combined;
    }

    public MaxsByRepsDTO mapFilteredMaxWeightsByReps(int exerciseId) {
        return mapFilteredMaxWeightsByReps(exerciseId, this::maxByExAndReps);
    }

    public MaxsByRepsDTO mapFilteredMaxWeightsByRepsForYear(int exerciseId, int year) {
        return mapFilteredMaxWeightsByReps(exerciseId, (exId, reps) -> maxByExAndRepsForYear(exId, reps, year));
    }

    private MaxsByRepsDTO mapFilteredMaxWeightsByReps(int exerciseId,
                                                      BiFunction<Integer, Integer, MaxWeightWithDateDTO> fetcher) {
        Map<Double, Map.Entry<Integer, MaxWeightWith1RMAndDateDTO>> weightToBestEntry = new HashMap<>();

        for (int nbReps = 1; nbReps <= 30; nbReps++) {
            MaxWeightWithDateDTO maxWeightWithDateDTO = fetcher.apply(exerciseId, nbReps);
            double weight = maxWeightWithDateDTO.maxWeight();
            if (weight == 0) continue;

            MaxWeightWith1RMAndDateDTO enriched = new MaxWeightWith1RMAndDateDTO(
                    weight,
                    calculateOneRepMax(nbReps, weight),
                    maxWeightWithDateDTO.date()
            );

            if (!weightToBestEntry.containsKey(weight) || nbReps > weightToBestEntry.get(weight).getKey()) {
                weightToBestEntry.put(weight, Map.entry(nbReps, enriched));
            }
        }

        Map<Integer, MaxWeightWith1RMAndDateDTO> filtered = new TreeMap<>();
        for (Map.Entry<Integer, MaxWeightWith1RMAndDateDTO> entry : weightToBestEntry.values()) {
            filtered.put(entry.getKey(), entry.getValue());
        }

        return new MaxsByRepsDTO(filtered);
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
