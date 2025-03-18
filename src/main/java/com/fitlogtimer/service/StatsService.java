package com.fitlogtimer.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Map<Integer, MaxWeightWithDateDTO> result = new HashMap<>();

        for (int nbReps : repNumbers) {
            MaxWeightWithDateDTO maxWeight = maxByExAndReps(exerciseId, nbReps);
            result.put(nbReps, maxWeight);
        }

        return new MaxsByRepsDTO(result);
    }
}
