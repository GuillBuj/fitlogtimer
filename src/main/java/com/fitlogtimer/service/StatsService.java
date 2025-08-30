package com.fitlogtimer.service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fitlogtimer.dto.stats.*;
import com.fitlogtimer.model.sets.BodyweightSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public List<CombinedMultiYearDTO> mergeMultiYearMaxsByReps(
            MaxsByRepsDTO personalBests,
            Map<Integer, MaxsByRepsDTO> bestsByYear
    ) {
        // Union de toutes les répétitions présentes dans les PB et les années
        Set<Integer> allReps = new TreeSet<>();
        allReps.addAll(personalBests.maxsByReps().keySet());
        for (MaxsByRepsDTO yearlyDTO : bestsByYear.values()) {
            allReps.addAll(yearlyDTO.maxsByReps().keySet());
        }

        List<CombinedMultiYearDTO> combined = new ArrayList<>();
        for (Integer reps : allReps) {
            // Collecte des meilleurs perfs pour cette rep dans chaque année
            Map<Integer, MaxWeightWith1RMAndDateDTO> bestsForRepByYear = new TreeMap<>();
            for (Map.Entry<Integer, MaxsByRepsDTO> entry : bestsByYear.entrySet()) {
                int year = entry.getKey();
                MaxWeightWith1RMAndDateDTO dto = entry.getValue().maxsByReps().get(reps);
                if (dto != null) {
                    bestsForRepByYear.put(year, dto);
                }
            }

            // Ajout du résultat combiné
            combined.add(new CombinedMultiYearDTO(
                    reps,
                    personalBests.maxsByReps().get(reps),
                    bestsForRepByYear
            ));
        }

        return combined;
    }

    public MaxsByRepsDTO mapFilteredMaxWeightsByReps(int exerciseId) {
        return mapFilteredMaxWeightsByReps(exerciseId, this::maxByExAndReps);
    }

    public MaxsByRepsDTO mapFilteredMaxWeightsByRepsForYear(int exerciseId, int year) {
        return mapFilteredMaxWeightsByReps(exerciseId, (exId, reps) -> maxByExAndRepsForYear(exId, reps, year));
    }

    public Map<Integer, MaxsByRepsDTO> mapFilteredMaxWeightsByRepsForAllYears(int exerciseId) {
        Integer firstYear = exerciseSetRepository.findFirstYearWithData(exerciseId);
        int currentYear = LocalDate.now().getYear();

        if (firstYear == null) {
            return Collections.emptyMap();
        }

        Map<Integer, MaxsByRepsDTO> result = new TreeMap<>();
        for (int year = firstYear; year <= currentYear; year++) {
            MaxsByRepsDTO dto = mapFilteredMaxWeightsByRepsForYear(exerciseId, year);
            result.put(year, dto);
        }

        return result;
    }

    private MaxsByRepsDTO mapFilteredMaxWeightsByReps(int exerciseId,
                                                      BiFunction<Integer, Integer, MaxWeightWithDateDTO> fetcher) {
        Map<Integer, MaxWeightWith1RMAndDateDTO> filtered = filterBestPerformances(exerciseId, fetcher);
        return new MaxsByRepsDTO(filtered);
    }

    private Map<Integer, MaxWeightWith1RMAndDateDTO> filterBestPerformances(
            int exerciseId,
            BiFunction<Integer, Integer, MaxWeightWithDateDTO> fetcher
    ) {
        Map<Integer, MaxWeightWith1RMAndDateDTO> bestByReps = new TreeMap<>();

        double currentMaxWeight = 0;

        // Parcours décroissant : des plus grosses reps aux plus petites
        for (int nbReps = 30; nbReps >= 1; nbReps--) {
            MaxWeightWithDateDTO dto = fetcher.apply(exerciseId, nbReps);
            double weight = dto.maxWeight();
            if (weight == 0) continue;

            // Si cette perf n'est pas éclipsée par une à + de reps
            if (weight > currentMaxWeight) {
                currentMaxWeight = weight;

                bestByReps.put(nbReps, new MaxWeightWith1RMAndDateDTO(
                        weight,
                        calculateOneRepMax(nbReps, weight),
                        dto.date()
                ));
            }
        }

        return bestByReps;
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

    public List<RecordHistoryItem> getMinimalRecordHistory(List<RecordHistoryItem> list) {
        if (list.isEmpty()) return Collections.emptyList();

        List<RecordHistoryItem> minimalRecordHistory = new ArrayList<>();
        Set<Double> seenWeights = new HashSet<>();

        RecordHistoryItem lastAdded = null;

        for (RecordHistoryItem current : list) {
            // garde la date la plus ancienne si meme poids
            if (seenWeights.contains(current.weight())) {
                continue;
            }

            // garde le plus lourd si meme date
            if (lastAdded != null && current.date().equals(lastAdded.date())) {
                if (current.weight() > lastAdded.weight()) {
                    minimalRecordHistory.set(minimalRecordHistory.size() - 1, current);
                    lastAdded = current;
                }
                continue;
            }

            minimalRecordHistory.add(current);
            seenWeights.add(current.weight());
            lastAdded = current;
        }

        return minimalRecordHistory;
    }

    public List<RecordHistoryItem> getRecordHistory(int exerciseId) {

        return getRecordHistory(exerciseId, 1);
    }

    // minRepsOrWeight selon le sous-type d'ExerciseSet
    public List<RecordHistoryItem> getRecordHistory(int exerciseId, int minRepsOrWeight) {
        List<ExerciseSet> sets = exerciseSetRepository.findAllSetsByDateAndMinReps(exerciseId, minRepsOrWeight);
        List<RecordHistoryItem> recordHistory = new ArrayList<>();

        double currentBestWeight = 0;
        double currentBestNbReps = 0;
        double bestWeightBodyweight = 0;

        for (ExerciseSet set : sets) {
            // On ne prend en compte que les FreeWeightSet
            if (set instanceof FreeWeightSet freeWeightSet && freeWeightSet.getWeight() != null) {
                double weight = freeWeightSet.getWeight();

                if (weight > currentBestWeight || (weight == currentBestWeight && set.getWorkout().getBodyWeight() < bestWeightBodyweight && set.getWorkout().getBodyWeight() != 0.0)) {
                    currentBestWeight = weight;
                    bestWeightBodyweight = set.getWorkout().getBodyWeight();

                    recordHistory.add(new RecordHistoryItem(
                            weight,
                            set.getRepNumber(),
                            set.getWorkout().getDate(),
                            bestWeightBodyweight,
                            set.getWorkout().getId()
                    ));
                }
            } else if (set instanceof BodyweightSet bodyweightSet) {
                int nbReps = bodyweightSet.getRepNumber();

                // Nouveau record uniquement si le nombre de reps est supérieur au précédent
                if (nbReps > currentBestNbReps) {
                    currentBestNbReps = nbReps;

                    recordHistory.add(new RecordHistoryItem(
                            bodyweightSet.getWeight(),
                            nbReps,
                            set.getWorkout().getDate(),
                            set.getWorkout().getBodyWeight(),
                            set.getWorkout().getId()
                    ));
                }
            }
        }

        Collections.reverse(recordHistory);

        return recordHistory;
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
