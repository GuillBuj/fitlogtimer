package com.fitlogtimer.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.stats.MaxWeightWith1RMAndDateDTO;
import com.fitlogtimer.dto.stats.MaxWeightWithDateDTO;
import com.fitlogtimer.dto.stats.MaxsByRepsDTO;
import com.fitlogtimer.model.ExerciseSet;

@Service
public class StatsService {

    @Autowired
    private ExerciseSetService exerciseSetService;
    
    public MaxWeightWithDateDTO maxByExAndReps(int exerciseId, int nbReps) {
        List<ExerciseSet> exerciseSets = exerciseSetService.getSetsByExerciseId(exerciseId);
    
        return exerciseSets.stream()
                .filter(set -> set.getRepNumber() >= nbReps)
                .max(Comparator.comparingDouble(ExerciseSet::getWeight))
                .map(set -> new MaxWeightWithDateDTO(set.getWeight(), set.getSession().getDate()))
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

    //=ARRONDI(SI(AL48<10;MOYENNE(AL47/(1,0278-0,0278*AL48);AL47*AL48^0,1);MOYENNE(AL47*(1+0,0333*AL48);AL47*AL48^0,1));1)
    //AL47: poids
    //AL48: reps
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
