package com.fitlogtimer.service;

import com.fitlogtimer.dto.ExerciseSetFor1RMCalcDTO;
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
import java.time.YearMonth;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;

import static com.fitlogtimer.service.StatsService.calculateOneRepMax;

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
        Set<String> filledPeriods = new HashSet<>();

        // Détermine la première et dernière année globale
        int globalFirstYear = Integer.MAX_VALUE;
        int globalLastYear = Integer.MIN_VALUE;
        for (Integer exerciseId : exerciseIds) {
            Integer firstYear = exerciseSetRepository.findFirstYearWithData(exerciseId);
            Integer lastYear = exerciseSetRepository.findLastYearWithData(exerciseId);
            if (firstYear == null || lastYear == null) continue;

            globalFirstYear = Math.min(globalFirstYear, firstYear);
            globalLastYear = Math.max(globalLastYear, lastYear);
        }
        if (globalFirstYear == Integer.MAX_VALUE) return result; // aucune donnée

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        globalLastYear = Math.max(globalLastYear, currentYear);

        Set<String> allPeriods = generateAllPeriods(globalFirstYear, globalLastYear, periodType);

        for (Integer exerciseId : exerciseIds) {
            String exerciseName = exerciseRepository.findById(exerciseId)
                    .map(Exercise::getName)
                    .orElse("Exercice " + exerciseId);

            double allTimeMax = 0.0;
            double yearMax = 0.0;
            int currentYearContext = -1;

            for (String period : allPeriods) {

                int periodYear = (periodType == PeriodType.MONTH)
                        ? Integer.parseInt(period.substring(0, 4))
                        : Integer.parseInt(period.split("-W")[0]);

                if (periodYear != currentYearContext) {
                    currentYearContext = periodYear;
                    yearMax = 0.0;
                }

                Double max = getMaxForPeriod(exerciseId, period, periodType);

                if (max != null && max > 0) {
                    RecordType recordType = RecordType.NONE;

                    if (max > allTimeMax) {
                        allTimeMax = max;
                        recordType = RecordType.PR;
                    }
                    else if (max > yearMax) {
                        recordType = RecordType.SB;
                    }

                    yearMax = Math.max(yearMax, max);

                    if (periodType == PeriodType.WEEK) {
                        String[] pp = period.split("-W");
                        int isoYear = Integer.parseInt(pp[0]);
                        int isoWeek = Integer.parseInt(pp[1]);

                        if (isoWeek == 1) {
                            LocalDate firstDayOfWeek = LocalDate
                                    .of(isoYear, 1, 4)
                                    .with(WeekFields.ISO.dayOfWeek(), 1);

                            if (firstDayOfWeek.getYear() < isoYear && recordType == RecordType.SB) {
                                recordType = RecordType.NONE;
                            }
                            yearMax = 0.0;
                        }
                    }

                    result.add(new ChartPeriodDataPointDTO(
                            period,
                            exerciseName,
                            max,
                            recordType
                    ));
                    filledPeriods.add(period);
                }
            }
        }

        addGhostPeriods(result, allPeriods, filledPeriods, periodType, today);

        log.info(result.stream().map(ChartPeriodDataPointDTO::toString).toList().toString());
        return result;
    }


    private Set<String> generateAllPeriods(int firstYear, int lastYear, PeriodType periodType) {
        Set<String> allPeriods = new LinkedHashSet<>();
        for (int year = firstYear; year <= lastYear; year++) {
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
                default -> throw new UnsupportedOperationException("Période non supportée");
            }
        }
        return allPeriods;
    }

    private Double getMaxForPeriod(int exerciseId, String periodLabel, PeriodType periodType) {
        if (periodType == PeriodType.MONTH) {
            String[] parts = periodLabel.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            return exerciseSetRepository.findMaxWeightByExerciseIdAndMonth(exerciseId, year, month);
        } else if (periodType == PeriodType.WEEK) {
            String[] parts = periodLabel.split("-W");
            int isoYear = Integer.parseInt(parts[0]);
            int isoWeek = Integer.parseInt(parts[1]);
            Double max = exerciseSetRepository.findMaxWeightByExerciseIdAndWeek(exerciseId, isoYear, isoWeek);

            // Court-circuit S53
            if ((isoWeek == 53 || isoWeek == 52) && (max == null || max == 0)) {
                max = exerciseSetRepository.findMaxWeightByExerciseIdAndWeek(exerciseId, isoYear + 1, 1);
            }
            return max;
        } else {
            throw new UnsupportedOperationException("Période non supportée");
        }
    }

    /* ******/
    /* pour 1RMest*/
    /* ******/
    public List<ChartPeriodDataPointDTO> getExercisesChartData1RMEst(
            List<String> exerciseShortNames,
            PeriodType periodType
    ) {
        return getExercisesChartData1RMEstInternal(exerciseShortNames, periodType);
    }

    private List<ChartPeriodDataPointDTO> getExercisesChartData1RMEstInternal(
            List<String> exerciseShortNames,
            PeriodType periodType
    ) {
        List<Integer> exerciseIds = exerciseSetRepository.findIdsByShortNames(exerciseShortNames);
        List<ChartPeriodDataPointDTO> result = new ArrayList<>();
        Set<String> filledPeriods = new HashSet<>();

        int globalFirstYear = Integer.MAX_VALUE;
        int globalLastYear = Integer.MIN_VALUE;

        for (Integer exerciseId : exerciseIds) {
            Integer firstYear = exerciseSetRepository.findFirstYearWithData(exerciseId);
            Integer lastYear = exerciseSetRepository.findLastYearWithData(exerciseId);
            if (firstYear == null || lastYear == null) continue;

            globalFirstYear = Math.min(globalFirstYear, firstYear);
            globalLastYear = Math.max(globalLastYear, lastYear);
        }

        if (globalFirstYear == Integer.MAX_VALUE) return result;

        LocalDate today = LocalDate.now();
        globalLastYear = Math.max(globalLastYear, today.getYear());

        Set<String> allPeriods = generateAllPeriods(globalFirstYear, globalLastYear, periodType);

        for (Integer exerciseId : exerciseIds) {

            String exerciseName = exerciseRepository.findById(exerciseId)
                    .map(Exercise::getName)
                    .orElse("Exercice " + exerciseId);

            double allTimeMax = 0.0;
            double yearMax = 0.0;
            int currentYearContext = -1;

            for (String period : allPeriods) {

                int periodYear = (periodType == PeriodType.MONTH)
                        ? Integer.parseInt(period.substring(0, 4))
                        : Integer.parseInt(period.split("-W")[0]);

                if (periodYear != currentYearContext) {
                    currentYearContext = periodYear;
                    yearMax = 0.0;
                }

                LocalDate start;
                LocalDate end;

                if (periodType == PeriodType.MONTH) {
                    int year = Integer.parseInt(period.substring(0, 4));
                    int month = Integer.parseInt(period.substring(5, 7));
                    YearMonth ym = YearMonth.of(year, month);
                    start = ym.atDay(1);
                    end = ym.atEndOfMonth();
                } else {
                    String[] pp = period.split("-W");
                    int isoYear = Integer.parseInt(pp[0]);
                    int isoWeek = Integer.parseInt(pp[1]);

                    start = LocalDate.of(isoYear, 1, 4)
                            .with(WeekFields.ISO.weekOfWeekBasedYear(), isoWeek)
                            .with(WeekFields.ISO.dayOfWeek(), 1);
                    end = start.plusDays(6);
                }

                List<ExerciseSetFor1RMCalcDTO> sets =
                        exerciseSetRepository.findAllSetsByExBetweenDates(exerciseId, start, end);

                Double max1RM = sets.stream()
                        .mapToDouble(s -> calculateOneRepMax(s.reps(), s.weight()))
                        .max()
                        .orElse(Double.NaN);

                if (!Double.isNaN(max1RM) && max1RM > 0) {

                    RecordType recordType = RecordType.NONE;

                    if (max1RM > allTimeMax) {
                        allTimeMax = max1RM;
                        recordType = RecordType.PR;
                    } else if (max1RM > yearMax) {
                        recordType = RecordType.SB;
                    }

                    yearMax = Math.max(yearMax, max1RM);

                    // Cas ISO week 1 qui chevauche l’année précédente
                    if (periodType == PeriodType.WEEK) {
                        String[] pp = period.split("-W");
                        int isoYear = Integer.parseInt(pp[0]);
                        int isoWeek = Integer.parseInt(pp[1]);

                        if (isoWeek == 1) {
                            LocalDate firstDayOfWeek = LocalDate
                                    .of(isoYear, 1, 4)
                                    .with(WeekFields.ISO.dayOfWeek(), 1);

                            if (firstDayOfWeek.getYear() < isoYear && recordType == RecordType.SB) {
                                recordType = RecordType.NONE;
                            }
                            yearMax = 0.0;
                        }
                    }

                    result.add(new ChartPeriodDataPointDTO(
                            period,
                            exerciseName,
                            Math.round(max1RM * 1000) / 1000.0,
                            recordType
                    ));

                    filledPeriods.add(period);
                }
            }
        }

        addGhostPeriods(result, allPeriods, filledPeriods, periodType, today);
        return result;
    }

    public List<ChartPeriodDataPointDTO> getMainLiftsChartDataMonthly1RMEst() {
        List<String> mainShortNames = List.of("DC", "DC30", "DCS", "DL", "HSQ", "DM", "DCP", "DCM", "DCX");
        List<ChartPeriodDataPointDTO> data =
                getExercisesChartData1RMEst(mainShortNames, PeriodType.MONTH);
        return mergeBenchPressVariants(data);
    }

    public List<ChartPeriodDataPointDTO> getMainLiftsChartDataWeekly1RMEst() {
        List<String> mainShortNames = List.of("DC", "DC30", "DCS", "DL", "HSQ", "DM", "DCP", "DCM", "DCX");
        List<ChartPeriodDataPointDTO> data =
                getExercisesChartData1RMEst(mainShortNames, PeriodType.WEEK);
        return mergeBenchPressVariants(data);
    }


    private void addGhostPeriods(List<ChartPeriodDataPointDTO> result, Set<String> allPeriods,
                                 Set<String> filledPeriods, PeriodType periodType, LocalDate today) {

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
                isFuture = (year > today.getYear()) || (year == today.getYear() && month > currentMonth);
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
