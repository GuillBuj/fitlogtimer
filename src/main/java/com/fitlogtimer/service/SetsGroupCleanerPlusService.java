package com.fitlogtimer.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fitlogtimer.dto.base.SetBasicDTO;
import com.fitlogtimer.dto.base.SetBasicInterfaceDTO;
import com.fitlogtimer.dto.base.SetBasicWith1RMDTO;
import com.fitlogtimer.dto.details.ExerciseDetailsGroupedDTO;
import com.fitlogtimer.dto.listitem.SetGroupCleanExerciseListItemDTO;
import com.fitlogtimer.dto.listitem.SetGroupCleanWorkoutListItemDTO;
import com.fitlogtimer.dto.postgroup.freeweight.SetsAllDifferentDTO;
import com.fitlogtimer.dto.postgroup.freeweight.SetsSameRepsDTO;
import com.fitlogtimer.dto.postgroup.freeweight.SetsSameWeightAndRepsDTO;
import com.fitlogtimer.dto.postgroup.freeweight.SetsSameWeightDTO;
import com.fitlogtimer.dto.transition.SetsGroupedWithNameDTO;
import com.fitlogtimer.model.ExerciseSet;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class SetsGroupCleanerPlusService {
 
    private final SetsGroupCleanerService setsGroupCleanerService; 

    public SetGroupCleanExerciseListItemDTO cleanWithMeta(
            LocalDate date,
            double bodyWeight,
            String type,
            SetsGroupedWithNameDTO setsGrouped,
            Set<String> selectedTypes
    ) {
       
        SetGroupCleanWorkoutListItemDTO cleaned;

        double est1RMmax = 0.0;
        double est1RMavg = 0.0;

        // si 'types' vide, tout inclure
        List<SetBasicInterfaceDTO> filteredSets;
        if (selectedTypes == null || selectedTypes.isEmpty()) {
            filteredSets = setsGrouped.sets();
        } else {
            filteredSets = setsGrouped.sets().stream()
                    .filter(set -> selectedTypes.contains(type))
                    .collect(Collectors.toList());
        }

        SetsGroupedWithNameDTO filteredSetsGrouped = new SetsGroupedWithNameDTO(
                setsGrouped.exerciseNameShort(),
                filteredSets
        );

        if (!filteredSetsGrouped.sets().isEmpty() && filteredSetsGrouped.sets().get(0) instanceof SetBasicWith1RMDTO) {
            est1RMmax = returnMax1RMest(filteredSetsGrouped);
            est1RMavg = calculateAvg1RMest(filteredSetsGrouped);

            cleaned = cleanSetsGroupForSetBasicWith1RM(filteredSetsGrouped);
        } else if (!filteredSetsGrouped.sets().isEmpty()) {
            cleaned = setsGroupCleanerService.cleanSetsGroup(filteredSetsGrouped);
        } else {
            cleaned = null;
            log.info("Aucun set trouvé pour ce filtre (types sélectionnés :)." );
        }

        Object cleanedSets = cleaned==null? null: cleaned.sets();

        return new SetGroupCleanExerciseListItemDTO(
            date,
            bodyWeight,
            type,
            cleanedSets,
            est1RMmax,
            est1RMavg       
        );
    }


    private SetGroupCleanWorkoutListItemDTO cleanSetsGroupForSetBasicWith1RM(SetsGroupedWithNameDTO sets) {
    List<SetBasicWith1RMDTO> sets1RM = sets.sets().stream()
            .map(s -> (SetBasicWith1RMDTO) s)
            .toList();

    if (hasSameWeight(sets1RM)) {
        if (hasSameReps(sets1RM)) {
            return new SetGroupCleanWorkoutListItemDTO(
                sets.exerciseNameShort(),
                new SetsSameWeightAndRepsDTO(
                    sets1RM.size(),
                    sets1RM.get(0).repNumber(),
                    sets1RM.get(0).weight()
                )
            );
        } else {
            List<Integer> reps = sets1RM.stream().map(SetBasicWith1RMDTO::repNumber).toList();
            return new SetGroupCleanWorkoutListItemDTO(
                sets.exerciseNameShort(),
                new SetsSameWeightDTO(sets1RM.get(0).weight(), reps)
            );
        }
    } else if (hasSameReps(sets1RM)) {
        List<Double> weights = sets1RM.stream().map(SetBasicWith1RMDTO::weight).toList();
        return new SetGroupCleanWorkoutListItemDTO(
            sets.exerciseNameShort(),
            new SetsSameRepsDTO(sets1RM.get(0).repNumber(), weights)
        );
    } else {
        // Transforme en DTO "simplifié" si nécessaire
        List<SetBasicDTO> converted = sets1RM.stream().map(SetBasicDTO::new).toList();
        return new SetGroupCleanWorkoutListItemDTO(
            sets.exerciseNameShort(),
            new SetsAllDifferentDTO(converted)
        );
    }
}

    public boolean hasSameWeight(List<SetBasicWith1RMDTO> sets) {
        if (sets.isEmpty()) return true;

        double firstWeight = sets.get(0).weight();
        for (SetBasicWith1RMDTO set : sets) {
            if (set.weight() != firstWeight) {
                return false;
            }
        }
        return true;
    }

    public boolean hasSameReps(List<SetBasicWith1RMDTO> sets) {
        if (sets.isEmpty()) return true;

        int firstReps = sets.get(0).repNumber();
        for (SetBasicWith1RMDTO set : sets) {
            if (set.repNumber() != firstReps) {
                return false;
            }
        }
        return true;
    }

    // private double estimate1RM(Object sets, String type) {
    //     if (sets instanceof List<?> list) {
    //         return switch (type) {
    //             case ExerciseSetType.FREE_WEIGHT, ExerciseSetType.BODYWEIGHT -> list.stream()
    //                     .filter(SetBasicDTO.class::isInstance)
    //                     .map(SetBasicDTO.class::cast)
    //                     .mapToDouble(set -> StatsService.calculateOneRepMax(set.repNumber(), set.weight()))
    //                     .max()
    //                     .orElse(0);

    //             default -> 0;
    //         };
    //     }
    //     return 0;
    // }

    private double returnMax1RMest(SetsGroupedWithNameDTO setsGrouped){
        return setsGrouped.sets().stream()
            .filter(set -> set instanceof SetBasicWith1RMDTO)
            .map(set -> (SetBasicWith1RMDTO) set)
            .mapToDouble(SetBasicWith1RMDTO::oneRepMax)
            .max()
            .orElse(0.0);
    }

    private double calculateAvg1RMest(SetsGroupedWithNameDTO setsGrouped){
        double est1RMavg = setsGrouped.sets().stream()
        .filter(set -> set instanceof SetBasicWith1RMDTO)
        .map(set -> (SetBasicWith1RMDTO) set)
        .mapToDouble(SetBasicWith1RMDTO::oneRepMax)
        .average()
        .orElse(0.0);
        
        return Math.round(est1RMavg * 100.0) / 100.0;
    }
}
