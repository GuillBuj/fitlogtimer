package com.fitlogtimer.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fitlogtimer.constants.ExerciseSetType;
import com.fitlogtimer.dto.ExerciseSetWithBodyWeightAndDateDTO;
import com.fitlogtimer.dto.ExerciseSetWithBodyWeightAndDateFor1RMDTO;
import com.fitlogtimer.dto.YearlyBestRatioFor1RMWithTrendDTO;
import com.fitlogtimer.dto.YearlyBestRatioWithTrendDTO;
import com.fitlogtimer.dto.stats.*;
import com.fitlogtimer.enums.Trend;
import com.fitlogtimer.mapper.ExerciseSetMapper;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.sets.BodyweightSet;
import com.fitlogtimer.model.sets.IsometricSet;
import com.fitlogtimer.repository.ExerciseRepository;
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
    private final ExerciseRepository exerciseRepository;
    private final ExercisePreferenceService exercisePreferenceService;

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

    public MaxWeightWithDateDTO maxWeightByExAndRepsForYear(int exerciseId, int repNumber, int year) {
        List<MaxWeightWithDateDTO> results = exerciseSetRepository
                .findMaxWeightByExerciseIdAndRepsAndYear(exerciseId, repNumber, year);

        return results.isEmpty()
                ? new MaxWeightWithDateDTO(0.0, null)
                : results.getFirst(); // la plus lourde
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

    public CombinedSimpleMultiYearDTO mergeSimpleMultiYearMaxs(
            Map<Integer, MaxWithDateDTO> yearlyMaxRecords) {

        // Trouver le record absolu (personal best)
        MaxWithDateDTO personalBest = yearlyMaxRecords.values().stream()
                .max(Comparator.comparingInt(MaxWithDateDTO::maxValue))
                .orElse(null);

        // Créer la map avec les tendances
        Map<Integer, YearlyBestSimpleWithTrendDTO> bestsByYearWithTrend = new TreeMap<>();

        for (Map.Entry<Integer, MaxWithDateDTO> entry : yearlyMaxRecords.entrySet()) {
            int year = entry.getKey();
            MaxWithDateDTO currentYearRecord = entry.getValue();

            Trend trend = calculateSimpleTrend(year, yearlyMaxRecords);
            YearlyBestSimpleWithTrendDTO yearlyBestWithTrend =
                    new YearlyBestSimpleWithTrendDTO(currentYearRecord, trend);

            bestsByYearWithTrend.put(year, yearlyBestWithTrend);
        }

        return new CombinedSimpleMultiYearDTO(personalBest, bestsByYearWithTrend);
    }

    public Trend calculateSimpleTrend(Integer year, Map<Integer, MaxWithDateDTO> yearlyMaxRecords) {
        if (year == null || yearlyMaxRecords == null) {
            return Trend.NEUTRAL;
        }

        MaxWithDateDTO current = yearlyMaxRecords.get(year);
        if (current == null) return Trend.NEUTRAL;

        MaxWithDateDTO previous = yearlyMaxRecords.get(year - 1);
        if (previous != null) {
            return calculateTrend(current.maxValue(), previous.maxValue());
        }

        return Trend.NEUTRAL;
    }

    private Trend calculateTrend(int currentValue, int previousValue) {
        if (previousValue == 0) {
            return Trend.NEUTRAL;
        }

        double percentageChange = ((currentValue - previousValue) / (double) previousValue) * 100;

        if (percentageChange >= 5.0) {
            return Trend.UP;
        } else if (percentageChange > 0) {
            return Trend.SLIGHTLY_UP;
        } else if (percentageChange == 0) {
            return Trend.NEUTRAL;
        } else if (percentageChange > -5.0) {
            return Trend.SLIGHTLY_DOWN;
        } else {
            return Trend.DOWN;
        }
    }

    public Optional<MaxWithDateDTO> getMaxValueForExercise(int exerciseId, String exerciseType){
        return switch (exerciseType) {
            case ExerciseSetType.BODYWEIGHT -> exerciseSetRepository.findMaxRepsWithDateByExerciseId(exerciseId);
            case ExerciseSetType.ISOMETRIC -> exerciseSetRepository.findMaxDurationWithDateByExerciseId(exerciseId);
            default -> Optional.empty();
        };
    }

    public Map<Integer, MaxWithDateDTO> getYearlyMaxRecords(int exerciseId, String exerciseType) {
        log.info("exerciseId: {}, exerciseType: {}", exerciseId, exerciseType);

        Map<Integer, MaxWithDateDTO> yearlyMaxRecords = new HashMap<>();

        Integer firstYear = exerciseSetRepository.findFirstYearWithData(exerciseId);

        if (firstYear == null) {
            return yearlyMaxRecords;
        }

        int currentYear = Year.now().getValue();

        for (int year = firstYear; year <= currentYear; year++) {

            Optional<MaxWithDateDTO> maxRecord = getMaxRecordForYear(exerciseId, exerciseType, year);

            if (maxRecord.isPresent()) {
                yearlyMaxRecords.put(year, maxRecord.get());
            }
        }

        return yearlyMaxRecords;
    }

    private Optional<MaxWithDateDTO> getMaxRecordForYear(int exerciseId, String exerciseType, int year) {

        Optional<MaxWithDateDTO> result = Optional.empty();

        try {
            if (ExerciseSetType.BODYWEIGHT.equals(exerciseType)) {
                result = exerciseSetRepository.findMaxRepsWithDateByExerciseIdAndYear(exerciseId, year);
                log.info("BODYWEIGHT query result: {}", result);
            } else if (ExerciseSetType.ISOMETRIC.equals(exerciseType)) {
                result = exerciseSetRepository.findMaxDurationWithDateByExerciseIdAndYear(exerciseId, year);
                log.info("ISOMETRIC query result: {}", result);
            } else {
                log.warn("Unknown exercise type: {}", exerciseType);
            }
        } catch (Exception e) {
            log.error("Error in getMaxRecordForYear", e);
        }

        return result;
    }

    public List<CombinedMultiYearDTO> mergeMultiYearMaxsByReps(
            MaxsByRepsDTO personalBests,
            Map<Integer, MaxsByRepsDTO> bestsByYear) {
        Set<Integer> allReps = new TreeSet<>();
        allReps.addAll(personalBests.maxsByReps().keySet());
        for (MaxsByRepsDTO yearlyDTO : bestsByYear.values()) {
            allReps.addAll(yearlyDTO.maxsByReps().keySet());
        }

        Map<Integer, CombinedMultiYearDTO> allRepsData = new HashMap<>();

        List<CombinedMultiYearDTO> combined = new ArrayList<>();
        for (Integer reps : allReps) {
            // Collecte des meilleurs perfs pour cette rep dans chaque année
            Map<Integer, YearlyBestWithTrendDTO> bestsForRepByYear = new TreeMap<>();
            for (Map.Entry<Integer, MaxsByRepsDTO> entry : bestsByYear.entrySet()) {
                int year = entry.getKey();
                MaxWeightWith1RMAndDateDTO dto = entry.getValue().maxsByReps().get(reps);

                if (dto != null) {

                    Trend trend = calculateTrend(year, reps, bestsByYear);

                    // Crée le YearlyBestWithTrendDTO pour chaque année avec la performance et la tendance
                    YearlyBestWithTrendDTO yearlyBestWithTrendDTO = new YearlyBestWithTrendDTO(dto, trend);
                    bestsForRepByYear.put(year, yearlyBestWithTrendDTO);
                }
            }

            MaxWeightWith1RMAndDateDTO personalBest = personalBests.maxsByReps().get(reps);
            Trend personalBestTrend = calculateTrend(0, reps, bestsByYear); // Tendance calculée pour le PB

            YearlyBestWithTrendDTO personalBestWithTrendDTO = new YearlyBestWithTrendDTO(personalBest, personalBestTrend);

            bestsForRepByYear.put(0, personalBestWithTrendDTO); // On ajoute l'année 0 pour le PB

            combined.add(new CombinedMultiYearDTO(
                    reps,
                    personalBest,
                    bestsForRepByYear
            ));
        }

        return combined;
    }

    public MaxsByRepsDTO mapFilteredMaxWeightsByReps(int exerciseId) {
        return mapFilteredMaxWeightsByReps(exerciseId, this::maxByExAndReps);
    }

    public MaxsByRepsDTO mapFilteredMaxWeightsByRepsForYear(int exerciseId, int year) {
        return mapFilteredMaxWeightsByReps(exerciseId, (exId, reps) -> maxWeightByExAndRepsForYear(exId, reps, year));
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
            BiFunction<Integer, Integer, MaxWeightWithDateDTO> fetcher) {
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
                rep -> maxWeightByExAndRepsForYear(exerciseId, rep, year));
    }

    public double getPersonalBest(int exerciseId){
        Double personalBest = exerciseSetRepository.findMaxWeightByExerciseId(exerciseId);
        return personalBest != null ? personalBest : 0.0;
    }

    public double getSeasonBestWeight(int exerciseId){
        Double personalBest = exerciseSetRepository.findMaxWeightByExerciseIdAndYear(exerciseId, LocalDate.now().getYear());
        return personalBest != null ? personalBest : 0.0;
    }

    public double getPersonalBestReps(int exerciseId){
        Integer maxReps = exerciseSetRepository.findMaxRepsByExerciseId(exerciseId);
        return (maxReps != null) ? maxReps.doubleValue() : 0.0;
    }

    public double getSeasonBestReps(int exerciseId){
        Integer maxReps = exerciseSetRepository.findMaxRepsByExerciseIdAndYear(exerciseId, LocalDate.now().getYear());
        return maxReps != null ? maxReps.doubleValue() : 0.0;
    }

    public double getPersonalBestDuration(int exerciseId){
        Integer maxDuration = exerciseSetRepository.findMaxDurationByExerciseId(exerciseId);
        return (maxDuration != null) ? maxDuration.doubleValue() : 0.0;
    }

    public double getSeasonBestDuration(int exerciseId){
        Integer seasonBest = exerciseSetRepository.findMaxDurationByExerciseIdAndYear(exerciseId, LocalDate.now().getYear());
        return seasonBest != null ? seasonBest.doubleValue() : 0.0;
    }

    public double getPersonalBestZero(int exerciseId){
        return Optional.ofNullable(exerciseSetRepository.findMaxRepsByExerciseId(exerciseId))
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
        int currentBestDurationS = 0;

        for (ExerciseSet set : sets) {
            if (set instanceof FreeWeightSet freeWeightSet && freeWeightSet.getWeight() != null) {
                double weight = freeWeightSet.getWeight();

                if (weight > currentBestWeight || (weight == currentBestWeight && set.getWorkout().getBodyWeight() < bestWeightBodyweight && set.getWorkout().getBodyWeight() != 0.0)) {
                    currentBestWeight = weight;
                    bestWeightBodyweight = set.getWorkout().getBodyWeight();

                    recordHistory.add(new RecordHistoryItem(
                            weight,
                            set.getRepNumber(),
                            0,
                            set.getWorkout().getDate(),
                            bestWeightBodyweight,
                            set.getWorkout().getId()
                    ));
                }
            } else if (set instanceof BodyweightSet bodyweightSet) {
                int nbReps = bodyweightSet.getRepNumber();

                // CORRECTION : Comparer avec le record actuel
                if (nbReps > currentBestNbReps) {
                    currentBestNbReps = nbReps;

                    recordHistory.add(new RecordHistoryItem(
                            0.0,
                            nbReps,
                            0,
                            set.getWorkout().getDate(),
                            set.getWorkout().getBodyWeight(),
                            set.getWorkout().getId()
                    ));
                }
            } else if (set instanceof IsometricSet isometricSet) {
                int durationS = isometricSet.getDurationS();
                log.info("durationS: {}", durationS);

                // CORRECTION : Comparer avec le record actuel
                if (durationS > currentBestDurationS) {
                    currentBestDurationS = durationS;
                    log.info("currentBestDurationS: {}", currentBestDurationS);

                    recordHistory.add(new RecordHistoryItem(
                            0.0,
                            0,
                            durationS,
                            set.getWorkout().getDate(),
                            set.getWorkout().getBodyWeight(),
                            set.getWorkout().getId()
                    ));
                }
            }
        }

        Collections.reverse(recordHistory);
        log.info("recordHistory: {}", recordHistory);
        return recordHistory;
    }

    public ExerciseSetWithBodyWeightAndDateDTO getTopMaxRatioSet(int exerciseId) {
        return exerciseSetRepository.findTopMaxRatioSet(exerciseId).orElse(null);
    }

    public Map<Integer, YearlyBestRatioWithTrendDTO> getTopMaxRatioSetByYears(int exerciseId) {
        List<ExerciseSetWithBodyWeightAndDateDTO> allSets =
                exerciseSetRepository.findYearlyMaxRatioSets(exerciseId);

        // Calcul du ratio et regroupement par année
        Map<Integer, ExerciseSetWithBodyWeightAndDateDTO> bestByYear = allSets.stream()
                .map(dto -> new ExerciseSetWithBodyWeightAndDateDTO(
                        dto.id(),
                        dto.exercise(),
                        dto.repNumber(),
                        dto.weight(),
                        dto.bodyWeight(),
                        dto.idWorkout(),
                        dto.workoutDate(),
                        dto.weight() / dto.bodyWeight()
                ))
                .collect(Collectors.toMap(
                        dto -> dto.workoutDate().getYear(),
                        Function.identity(),
                        (existing, replacement) -> replacement.ratio() > existing.ratio() ? replacement : existing
                ));

        List<Map.Entry<Integer, ExerciseSetWithBodyWeightAndDateDTO>> sortedEntries =
                new ArrayList<>(bestByYear.entrySet());

        sortedEntries.sort(Map.Entry.comparingByKey());

        Map<Integer, YearlyBestRatioWithTrendDTO> result = new LinkedHashMap<>();

        for (int i = 0; i < sortedEntries.size(); i++) {
            Map.Entry<Integer, ExerciseSetWithBodyWeightAndDateDTO> entry = sortedEntries.get(i);
            int year = entry.getKey();
            ExerciseSetWithBodyWeightAndDateDTO current = entry.getValue();

            Trend trend;
            if (i == 0) {
                trend = Trend.NEUTRAL; // Première année
            } else {
                ExerciseSetWithBodyWeightAndDateDTO previous = sortedEntries.get(i - 1).getValue();
                trend = calculateTrend(current.ratio(), previous.ratio());
            }

            result.put(year, new YearlyBestRatioWithTrendDTO(current, trend));
        }

        return result;
    }

    public ExerciseSetWithBodyWeightAndDateFor1RMDTO getTop1RMRatioSet(int exerciseId) {
        List<ExerciseSetWithBodyWeightAndDateFor1RMDTO> allSets =
                exerciseSetRepository.findAllSetsFor1RM(exerciseId);

        return allSets.stream()
                .map(dto -> new ExerciseSetWithBodyWeightAndDateFor1RMDTO(
                        dto.id(),
                        dto.exercise(),
                        dto.repNumber(),
                        dto.weight(),
                        dto.bodyWeight(),
                        dto.idWorkout(),
                        dto.workoutDate(),
                        calculateOneRepMax(dto.repNumber(), dto.weight()),
                        calculateOneRepMax(dto.repNumber(), dto.weight()) / dto.bodyWeight()
                ))
                .max(Comparator.comparingDouble(ExerciseSetWithBodyWeightAndDateFor1RMDTO::ratio))
                .orElse(null);
    }

    public Map<Integer, YearlyBestRatioFor1RMWithTrendDTO> getTop1RMRatioSetByYears(int exerciseId) {

        List<ExerciseSetWithBodyWeightAndDateFor1RMDTO> allSets =
                exerciseSetRepository.findAllSetsFor1RM(exerciseId);

        // Calcul est1RM et ratio
        List<ExerciseSetWithBodyWeightAndDateFor1RMDTO> computedSets = allSets.stream()
                .map(dto -> new ExerciseSetWithBodyWeightAndDateFor1RMDTO(
                        dto.id(),
                        dto.exercise(),
                        dto.repNumber(),
                        dto.weight(),
                        dto.bodyWeight(),
                        dto.idWorkout(),
                        dto.workoutDate(),
                        calculateOneRepMax(dto.repNumber(), dto.weight()),
                        calculateOneRepMax(dto.repNumber(), dto.weight()) / dto.bodyWeight()
                ))
                .toList();

        // Regroupement par année avec le meilleur ratio
        Map<Integer, ExerciseSetWithBodyWeightAndDateFor1RMDTO> bestByYear = computedSets.stream()
                .collect(Collectors.toMap(
                        dto -> dto.workoutDate().getYear(),
                        Function.identity(),
                        (existing, replacement) -> replacement.ratio() > existing.ratio() ? replacement : existing
                ));

        // Trier les années pour calculer les trends
        List<Map.Entry<Integer, ExerciseSetWithBodyWeightAndDateFor1RMDTO>> sortedEntries =
                new ArrayList<>(bestByYear.entrySet());
        sortedEntries.sort(Map.Entry.comparingByKey());

        Map<Integer, YearlyBestRatioFor1RMWithTrendDTO> result = new LinkedHashMap<>();

        for (int i = 0; i < sortedEntries.size(); i++) {
            Map.Entry<Integer, ExerciseSetWithBodyWeightAndDateFor1RMDTO> entry = sortedEntries.get(i);
            int year = entry.getKey();
            ExerciseSetWithBodyWeightAndDateFor1RMDTO current = entry.getValue();

            Trend trend;
            if (i == 0) {
                trend = Trend.NEUTRAL; // Première année
            } else {
                ExerciseSetWithBodyWeightAndDateFor1RMDTO previous = sortedEntries.get(i - 1).getValue();
                trend = calculateTrend(current.ratio(), previous.ratio());
            }

            result.put(year, new YearlyBestRatioFor1RMWithTrendDTO(current, trend));
        }

        return result;
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

    public Trend calculateTrend(Integer year, Integer nbReps, Map<Integer, MaxsByRepsDTO> bestsByYear) {
        if (year == null || nbReps == null || bestsByYear == null) {
            return Trend.NEUTRAL;
        }

        // Récupère l'entrée des meilleures performances pour l'année en cours
        MaxsByRepsDTO currentYearData = bestsByYear.get(year);
        if (currentYearData == null) return Trend.NEUTRAL;

        // Récupère la performance pour la répétition donnée (nbReps)
        MaxWeightWith1RMAndDateDTO current = currentYearData.maxsByReps().get(nbReps);
        if (current == null) return Trend.NEUTRAL;

        // Compare avec l'année précédente (même nbReps si possible)
        MaxsByRepsDTO previousYearData = bestsByYear.get(year - 1);
        if (previousYearData != null) {
            MaxWeightWith1RMAndDateDTO previous = previousYearData.maxsByReps().get(nbReps);

            // Si on trouve une performance pour la même répétition
            if (previous != null) {
                return calculateTrend(current.maxWeight(), previous.maxWeight());
            } else {
                // Si on ne trouve pas la performance pour la répétition exacte, on compare avec des répétitions plus élevées
                return compareWithHigherReps(current, year, nbReps, bestsByYear);
            }
        }

        return Trend.NEUTRAL;
    }

    private Trend compareWithHigherReps(MaxWeightWith1RMAndDateDTO current, Integer year, Integer nbReps, Map<Integer, MaxsByRepsDTO> bestsByYear) {

        for (int higherReps = nbReps + 1; higherReps <= 30; higherReps++) {
            MaxsByRepsDTO previousYearData = bestsByYear.get(year - 1);
            if (previousYearData != null) {
                MaxWeightWith1RMAndDateDTO previousHigherReps = previousYearData.maxsByReps().get(higherReps);
                if (previousHigherReps != null) {
                    // Si on trouve une performance plus élevée pour l'année précédente, on compare avec celle-ci
                    return calculateTrend(current.maxWeight(), previousHigherReps.maxWeight());
                }
            }
        }

        return Trend.NEUTRAL;
    }

    private Trend calculateTrend(double current, double previous) {

        if (previous == 0) {
            return Trend.NEUTRAL;
        }

        double percentageChange = ((current - previous) / previous) * 100;

        if (percentageChange >= 5.0) {
            return Trend.UP;
        } else if (percentageChange > 0) {
            return Trend.SLIGHTLY_UP;
        } else if (percentageChange == 0) {
            return Trend.NEUTRAL;
        } else if (percentageChange > -5.0) {
            return Trend.SLIGHTLY_DOWN;
        } else {
            return Trend.DOWN;
        }
    }

    //Pour tableau récap des gros exs
    public List<ExerciseYearlyMaxTableDTO> getPeriodMaxTableForAllVisible() throws IOException {
        List<Exercise> visibleExercises = exercisePreferenceService.getVisibleExercises("main");

        return visibleExercises.stream()
                .map(exercise -> {
                    Map<Integer, PeriodMaxWithTrendDTO> yearlyData = getPeriodMaxWithTrend(exercise.getId());
                    return new ExerciseYearlyMaxTableDTO(
                            exercise.getName(),
                            exercise.getId(),
                            yearlyData
                    );
                })
                .collect(Collectors.toList());
    }

    public Map<Integer, PeriodMaxWithTrendDTO> getPeriodMaxWithTrend(int exerciseId) {
        List<PeriodMaxDTO> yearlyMax = exerciseSetRepository.findYearlyMaxList(exerciseId);

        // filtrage des doublons
        List<PeriodMaxDTO> filteredMax = yearlyMax.stream()
                .collect(Collectors.toMap(
                        PeriodMaxDTO::year,
                        dto -> dto,
                        (existing, replacement) -> existing
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(PeriodMaxDTO::year))
                .collect(Collectors.toList());

        Map<Integer, PeriodMaxWithTrendDTO> result = new LinkedHashMap<>();

        for (int i = 0; i < filteredMax.size(); i++) {
            PeriodMaxDTO current = filteredMax.get(i);
            Double trendRatio = calculateTrendRatioGeneric(filteredMax, i, PeriodMaxDTO::maxValue);
            String color = computeTrendColor(trendRatio);

            result.put(current.year(), new PeriodMaxWithTrendDTO(
                    current.maxValue(),
                    current.bodyweight(),
                    current.workoutId(),
                    current.year(),
                    trendRatio,
                    color
            ));
        }

        return sortByYearDesc(result);
    }

    public List<ExerciseYearlyMaxRatioTableDTO> getPeriodMaxRatioTableForAllVisible() throws IOException {
        List<Exercise> visibleExercises = exercisePreferenceService.getVisibleExercises("main");

        return visibleExercises.stream()
                .map(exercise -> {
                    Map<Integer, PeriodMaxRatioWithTrendDTO> yearlyData = getPeriodMaxRatioWithTrend(exercise.getId());
                    return new ExerciseYearlyMaxRatioTableDTO(
                            exercise.getName(),
                            exercise.getId(),
                            yearlyData
                    );
                })
                .collect(Collectors.toList());
    }

    public Map<Integer, PeriodMaxRatioWithTrendDTO> getPeriodMaxRatioWithTrend(int exerciseId) {
        // On réutilise la requête que tu as modifiée précédemment :
        List<PeriodMaxRatioDTO> yearlyRatios = exerciseSetRepository.findYearlyMaxRatioList(exerciseId);

        // même logique que pour les max
        List<PeriodMaxRatioDTO> filteredMaxRatios = yearlyRatios.stream()
                .collect(Collectors.toMap(
                        PeriodMaxRatioDTO::year,
                        dto -> dto,
                        (existing, replacement) -> existing
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(PeriodMaxRatioDTO::year))
                .collect(Collectors.toList());

        Map<Integer, PeriodMaxRatioWithTrendDTO> result = new LinkedHashMap<>();

        for (int i = 0; i < filteredMaxRatios.size(); i++) {
            PeriodMaxRatioDTO current = filteredMaxRatios.get(i);
            Double trendRatio = calculateTrendRatioGeneric(filteredMaxRatios, i, PeriodMaxRatioDTO::ratio);
            String color = computeTrendColor(trendRatio);

            result.put(current.year(), new PeriodMaxRatioWithTrendDTO(
                    current.maxValue(),
                    current.bodyweight(),
                    Math.round(current.ratio() * 1000) / 1000.0,
                    current.workoutId(),
                    current.year(),
                    trendRatio,
                    color
            ));
        }

        return sortByYearDesc(result);
    }

    private String computeTrendColor(Double trendRatio) {
        if (trendRatio == null) return "";

        // Cas spécial pour -1 (données manquantes)
        if (trendRatio == -1.0) {
            return "background-color: #f6f6d6;";
        }

        boolean isPositive = trendRatio >= 1;
        double hue = isPositive ? 130 : 0;

        // Écart à 1 - courbe logarithmique pour plus de sensibilité sur petites variations
        double diff = Math.abs(trendRatio - 1);

        // Utilisation d'une courbe pour amplifier les petites différences
        double intensity;
        if (diff < 0.05) {
            // Amplification forte pour les très petites variations
            intensity = diff * 150;
        } else if (diff < 0.2) {
            // Amplification moyenne
            intensity = 7.5 + (diff - 0.05) * 80;
        } else {
            // Limitation pour éviter le flashy
            intensity = 19.5 + (diff - 0.2) * 10;
        }

        intensity = Math.min(intensity, 35); // Plafond bas pour éviter le flashy

        double saturation = 45 + intensity;  // 45% à 80%
        double lightness = 96 - (intensity * 1.4); // 96% à 47% - grande plage

        return String.format("background-color: hsl(%.0f, %.0f%%, %.0f%%);", hue, saturation, lightness);
    }

    private <T> Double calculateTrendRatioGeneric(List<T> data, int currentIndex, Function<T, Double> valueExtractor) {
        T current = data.get(currentIndex);
        Double currentValue = valueExtractor.apply(current);

        for (int i = currentIndex - 1; i >= 0; i--) {
            T previous = data.get(i);
            Double prevValue = valueExtractor.apply(previous);

            if (prevValue != null && prevValue > 0) {
                double ratio = currentValue / prevValue;
                return Math.round(ratio * 1000.0) / 1000.0;
            }
        }

        return -1.0;
    }

    private <T> Map<Integer, T> sortByYearDesc(Map<Integer, T> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.<Integer, T>comparingByKey().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }
}
