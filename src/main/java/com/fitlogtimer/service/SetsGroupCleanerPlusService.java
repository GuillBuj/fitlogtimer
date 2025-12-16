package com.fitlogtimer.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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
        Double est1RMbest3Avg = 0.0;
        Double max = 0.0;
        Double volume = 0.0;

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
            est1RMbest3Avg = calculateBest3Avg1RMest(filteredSetsGrouped);
            max = returnMax(filteredSetsGrouped);
            volume = calculateVolume(filteredSetsGrouped);

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
                est1RMbest3Avg,
                max,
                volume
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
        return new SetGroupCleanWorkoutListItemDTO(
                sets.exerciseNameShort(),
                buildSetsAllDifferent(sets1RM)
        );
    }
}

    private SetsAllDifferentDTO buildSetsAllDifferent(
            List<SetBasicWith1RMDTO> sets
    ) {
        List<Object> result = new ArrayList<>();

        int i = 0;
        while (i < sets.size()) {
            SetBasicWith1RMDTO first = sets.get(i);
            int reps = first.repNumber();

            List<SetBasicWith1RMDTO> block = new ArrayList<>();
            block.add(first);

            int j = i + 1;
            while (j < sets.size() && sets.get(j).repNumber() == reps) {
                block.add(sets.get(j));
                j++;
            }

            boolean sameWeight = block.stream()
                    .map(SetBasicWith1RMDTO::weight)
                    .distinct()
                    .count() == 1;

            if (sameWeight) {
                result.add(new SetsSameWeightAndRepsDTO(
                        block.size(),
                        reps,
                        block.get(0).weight()
                ));
            } else {
                result.add(new SetsSameRepsDTO(
                        reps,
                        block.stream().map(SetBasicWith1RMDTO::weight).toList()
                ));
            }

            i = j;
        }

        return new SetsAllDifferentDTO(result);
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

    private Double returnMax(SetsGroupedWithNameDTO setsGrouped) {
        return setsGrouped.sets().stream()
                .filter(set -> set instanceof SetBasicWith1RMDTO)
                .map(set -> (SetBasicWith1RMDTO) set)
                .mapToDouble(SetBasicWith1RMDTO::weight)
                .max()
                .orElse(0.0);
    }

    private double returnMax1RMest(SetsGroupedWithNameDTO setsGrouped){
        return setsGrouped.sets().stream()
            .filter(set -> set instanceof SetBasicWith1RMDTO)
            .map(set -> (SetBasicWith1RMDTO) set)
            .mapToDouble(SetBasicWith1RMDTO::oneRepMax)
            .max()
            .orElse(0.0);
    }

    private Double calculateBest3Avg1RMest(SetsGroupedWithNameDTO setsGrouped) {
        List<Double> top3 = setsGrouped.sets().stream()
                .filter(set -> set instanceof SetBasicWith1RMDTO)
                .map(set -> (SetBasicWith1RMDTO) set)
                .map(SetBasicWith1RMDTO::oneRepMax)
                .sorted(Comparator.reverseOrder()) // tri décroissant
                .limit(3)
                .toList();

        if (top3.size() < 3) {
            return null;
        }

        double avg = top3.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        return Math.round(avg * 100.0) / 100.0;
    }

    private double calculateVolume(SetsGroupedWithNameDTO setsGrouped) {
        return Math.floor(setsGrouped.sets().stream()
            .filter(set -> set instanceof SetBasicWith1RMDTO)
            .map(set -> (SetBasicWith1RMDTO) set)
            .mapToDouble(set -> set.weight() * set.repNumber())
            .sum());
    }
}
