package com.fitlogtimer.service;

import com.fitlogtimer.dto.chart.ChartDataPointDTO;
import com.fitlogtimer.dto.chart.ChartPeriodDataPointDTO;
import com.fitlogtimer.dto.details.ExerciseDetailsGroupedDTO;
import com.fitlogtimer.enums.PeriodType;
import com.fitlogtimer.enums.RecordType;
import com.fitlogtimer.mapper.ChartDataMapper;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.repository.ExerciseSetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChartService {

    private final ChartDataMapper chartDataMapper;
    private final ExerciseSetRepository exerciseSetRepository;
    private final ExerciseRepository exerciseRepository;

    public List<ChartDataPointDTO> getProgressChartData(ExerciseDetailsGroupedDTO exerciseDetails) {
        if (exerciseDetails == null || exerciseDetails.exerciseSets() == null) {
            return Collections.emptyList();
        }

        return chartDataMapper.toChartDataPoints(exerciseDetails.exerciseSets());
    }

    private List<ChartPeriodDataPointDTO> getExercisesChartData(List<String> exerciseShortNames, PeriodType periodType) {

        List<Integer> exerciseIds = exerciseSetRepository.findIdsByShortNames(exerciseShortNames);
        List<ChartPeriodDataPointDTO> result = new ArrayList<>();

        int globalFirstYear = Integer.MAX_VALUE;
        int globalLastYear = Integer.MIN_VALUE;

        for (Integer exerciseId : exerciseIds) {
            Integer firstYear = exerciseSetRepository.findFirstYearWithData(exerciseId);
            Integer lastYear = exerciseSetRepository.findLastYearWithData(exerciseId);
            if (firstYear == null || lastYear == null) continue;

            globalFirstYear = Math.min(globalFirstYear, firstYear);
            globalLastYear = Math.max(globalLastYear, lastYear);
        }

        if (globalFirstYear == Integer.MAX_VALUE) {
            return result; // Aucune donnée
        }

        // Ajoute aussi l'année en cours si plus récente
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        globalLastYear = Math.max(globalLastYear, currentYear);

        // Construit la liste des périodes globales
        Set<String> allPeriods = new LinkedHashSet<>();
        for (int year = globalFirstYear; year <= globalLastYear; year++) {
            switch (periodType) {
                case MONTH -> {
                    for (int m = 1; m <= 12; m++) {
                        allPeriods.add(String.format("%d-%02d", year, m));
                    }
                }
                case WEEK -> {
                    WeekFields wf = WeekFields.ISO;
                    LocalDate cursor = LocalDate.of(year, 1, 4).with(wf.dayOfWeek(), 1);
                    LocalDate endDate = LocalDate.of(year, 12, 31);
                    while (!cursor.isAfter(endDate)) {
                        int isoYear = cursor.get(wf.weekBasedYear());
                        int isoWeek = cursor.get(wf.weekOfWeekBasedYear());
                        allPeriods.add(String.format("%d-W%02d", isoYear, isoWeek));
                        cursor = cursor.plusWeeks(1);
                    }
                }
                case YEAR, SEMESTER, QUARTER -> throw new UnsupportedOperationException("Période non supportée");
            }
        }

        Set<String> filledPeriods = new HashSet<>();

        for (Integer exerciseId : exerciseIds) {
            String exerciseName = exerciseRepository.findById(exerciseId)
                    .map(Exercise::getName)
                    .orElse("Exercice " + exerciseId);

            Integer firstYear = exerciseSetRepository.findFirstYearWithData(exerciseId);
            Integer lastYear = exerciseSetRepository.findLastYearWithData(exerciseId);
            if (firstYear == null || lastYear == null) continue;

            double allTimeMax = 0.0;

            for (int year = firstYear; year <= lastYear; year++) {
                double yearMax = 0.0;

                if (periodType == PeriodType.WEEK) {
                    WeekFields wf = WeekFields.ISO;
                    LocalDate cursor = LocalDate.of(year, 1, 4).with(wf.dayOfWeek(), 1);
                    LocalDate endDate = LocalDate.of(year, 12, 31);

                    while (!cursor.isAfter(endDate)) {
                        int isoYear = cursor.get(wf.weekBasedYear());
                        int isoWeek = cursor.get(wf.weekOfWeekBasedYear());
                        String periodLabelW = String.format("%d-W%02d", isoYear, isoWeek);

                        Double max = exerciseSetRepository.findMaxWeightByExerciseIdAndWeek(exerciseId, isoYear, isoWeek);

                        // Court-circuit S53 → semaine 1 de l'année suivante
                        if ((isoWeek == 53 || isoWeek == 52) && (max == null || max == 0)) {
                            max = exerciseSetRepository.findMaxWeightByExerciseIdAndWeek(exerciseId, isoYear + 1, 1);
                        }

                        if (max != null && max > 0) {
                            RecordType recordType = RecordType.NONE;
                            if (max > allTimeMax) {
                                allTimeMax = max;
                                recordType = RecordType.PR;
                            } else if (max > yearMax) {
                                recordType = RecordType.SB;
                            }
                            yearMax = Math.max(yearMax, max);

                            result.add(new ChartPeriodDataPointDTO(
                                    periodLabelW,
                                    exerciseName,
                                    max,
                                    recordType
                            ));

                            filledPeriods.add(periodLabelW);
                        }

                        cursor = cursor.plusWeeks(1);
                    }

                } else if (periodType == PeriodType.MONTH) {
                    for (int m = 1; m <= 12; m++) {
                        String periodLabel = String.format("%d-%02d", year, m);
                        Double max = exerciseSetRepository.findMaxWeightByExerciseIdAndMonth(exerciseId, year, m);

                        if (max != null && max > 0) {
                            RecordType recordType = RecordType.NONE;
                            if (max > allTimeMax) {
                                allTimeMax = max;
                                recordType = RecordType.PR;
                            } else if (max > yearMax) {
                                recordType = RecordType.SB;
                            }
                            yearMax = Math.max(yearMax, max);

                            result.add(new ChartPeriodDataPointDTO(
                                    periodLabel,
                                    exerciseName,
                                    max,
                                    recordType
                            ));

                            filledPeriods.add(periodLabel);
                        }
                    }
                } else {
                    throw new UnsupportedOperationException("Période non supportée");
                }
            }
        }

        // Ajout des _GHOST_ pour les périodes sans données
        int currentMonth = today.getMonthValue();
        WeekFields wf = WeekFields.ISO;
        int currentWeek = today.get(wf.weekOfWeekBasedYear());
        int currentWeekYear = today.get(wf.weekBasedYear());

        for (String period : allPeriods) {
            if (filledPeriods.contains(period)) continue;

            boolean isFuture = false;
            if (periodType == PeriodType.MONTH) {
                String[] parts = period.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                isFuture = (year > currentYear) || (year == currentYear && month > currentMonth);
            } else if (periodType == PeriodType.WEEK) {
                String[] parts = period.split("-W");
                int year = Integer.parseInt(parts[0]);
                int week = Integer.parseInt(parts[1]);
                isFuture = (year > currentWeekYear) || (year == currentWeekYear && week > currentWeek);
            }

            if (!isFuture) {
                result.add(new ChartPeriodDataPointDTO(
                        period,
                        "_GHOST_",
                        0.0,
                        RecordType.NONE
                ));
            }
        }

        log.info(result.stream().map(ChartPeriodDataPointDTO::toString).toList().toString());
        return result;
    }


    public List<ChartPeriodDataPointDTO> getMainLiftsChartDataMonthly() {
        List<String> mainShortNames = List.of("DC", "DC30", "DCS", "DL", "HSQ", "DM", "DCP", "DCM", "DCX");
        List<ChartPeriodDataPointDTO> data = getExercisesChartData(mainShortNames, PeriodType.MONTH);
        return mergeBenchPressVariants(data);
    }

    public List<ChartPeriodDataPointDTO> getMainLiftsChartDataWeekly() {
        List<String> mainShortNames = List.of("DC", "DC30", "DCS", "DL", "HSQ", "DM", "DCP", "DCM", "DCX");
        List<ChartPeriodDataPointDTO> data = getExercisesChartData(mainShortNames, PeriodType.WEEK);
        return mergeBenchPressVariants(data);
    }

    public List<ChartPeriodDataPointDTO> getBenchLiftsChartDataMonthly() {
        List<String> exerciseShortNames = List.of("DC", "DC30", "DCS", "DCP", "DCM", "DCX");
        return getExercisesChartData(exerciseShortNames, PeriodType.MONTH);
    }

    public List<ChartPeriodDataPointDTO> getBenchLiftsChartDataWeekly() {
        List<String> exerciseShortNames = List.of("DC", "DC30", "DCS", "DCP", "DCM", "DCX");
        return getExercisesChartData(exerciseShortNames, PeriodType.WEEK);
    }

    private List<ChartPeriodDataPointDTO> mergeBenchPressVariants(List<ChartPeriodDataPointDTO> dataPoints) {
        Set<String> benchVariants = Set.of(
                "Bench Press",
                "Paused Bench Press",
                "Mid Grip Bench Press",
                "Bench Press Speed"
        );

        Map<String, List<ChartPeriodDataPointDTO>> groupedByPeriod = new LinkedHashMap<>();

        for (ChartPeriodDataPointDTO dp : dataPoints) {
            String period = dp.period();
            String groupName = benchVariants.contains(dp.exerciseName()) ? "Bench Press(+)" : dp.exerciseName();
            groupedByPeriod.computeIfAbsent(period + "_" + groupName, k -> new ArrayList<>()).add(dp);
        }

        List<ChartPeriodDataPointDTO> merged = new ArrayList<>();

        for (List<ChartPeriodDataPointDTO> group : groupedByPeriod.values()) {
            if (group.isEmpty()) continue;

            ChartPeriodDataPointDTO first = group.getFirst();
            String period = first.period();
            String groupName = benchVariants.contains(first.exerciseName()) ? "Bench Press(+)" : first.exerciseName();

            double max = group.stream().mapToDouble(ChartPeriodDataPointDTO::max).max().orElse(0.0);
            RecordType recordType;

            if ("Bench Press(+)".equals(groupName)) {
                recordType = group.stream()
                        .filter(dp -> "Bench Press".equals(dp.exerciseName()))
                        .map(ChartPeriodDataPointDTO::recordType)
                        .max(Comparator.comparing(Enum::ordinal))
                        .orElse(RecordType.NONE);
            } else {
                // pour les autres(non mergés)
                recordType = group.stream()
                        .map(ChartPeriodDataPointDTO::recordType)
                        .max(Comparator.comparing(Enum::ordinal))
                        .orElse(RecordType.NONE);
            }

            merged.add(new ChartPeriodDataPointDTO(period, groupName, max, recordType));
        }

        return merged;
    }

}
