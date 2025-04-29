package com.fitlogtimer.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
public class StatsService {

    private final ExerciseSetRepository exerciseSetRepository;
    
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

    public MaxsByRepsDTO mapMaxWeightsByReps(int exerciseId, List<Integer> repNumbers) {
        Map<Integer, MaxWeightWith1RMAndDateDTO> result = new HashMap<>();

        for (int nbReps : repNumbers) {
            MaxWeightWithDateDTO maxWeightWithDateDTO = maxByExAndReps(exerciseId, nbReps);
            double weight = maxWeightWithDateDTO.maxWeight();
            MaxWeightWith1RMAndDateDTO maxWeight = new MaxWeightWith1RMAndDateDTO(weight, calculateOneRepMax(nbReps, weight),maxWeightWithDateDTO.date());
            result.put(nbReps, maxWeight);
        }

        return new MaxsByRepsDTO(result);
    }

    public double getPersonalBest(int exerciseId){
        
        return maxByExAndReps(exerciseId, 1).maxWeight();
    }

    public double getPersonalBestDuration(int exerciseId){

        return exerciseSetRepository.findMaxDurationByExerciseId(exerciseId).doubleValue();
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

    //1RMest d'après un mix de 3 formules trouvées sur le net
    public static double calculateOneRepMax(int repNumber, double weight){
        
        double oneRepMax1 = weight * Math.pow(repNumber, 0.1);

        double oneRepMax;

        if(repNumber<10){
            oneRepMax = ((weight / (1.0278 - (0.0278 * repNumber))) + oneRepMax1) / 2;
        } else {
            oneRepMax = ((weight * (1 + (0.0333 * repNumber))) + oneRepMax1) / 2;
        }

        return Math.round(oneRepMax * 10) / 10.0;
    }
}
