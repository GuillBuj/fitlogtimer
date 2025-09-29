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
import java.time.YearMonth;
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

    private List<ChartPeriodDataPointDTO> getMainLiftsChartData(PeriodType periodType) {
        List<String> mainShortNames = List.of("DC", "DC30", "DCS", "DL", "HSQ", "DM");
        List<Integer> exerciseIds = exerciseSetRepository.findIdsByShortNames(mainShortNames);

        List<ChartPeriodDataPointDTO> result = new ArrayList<>();

        // construire la liste des périodes globales
        Set<String> allPeriods = new LinkedHashSet<>();
        for (Integer exerciseId : exerciseIds) {
            Integer firstYear = exerciseSetRepository.findFirstYearWithData(exerciseId);
            Integer lastYear = exerciseSetRepository.findLastYearWithData(exerciseId);
            if (firstYear == null || lastYear == null) continue;

            for (int year = firstYear; year <= lastYear; year++) {
                int maxPeriods = switch (periodType) {
                    case MONTH -> 12;
                    case WEEK -> Year.of(year).length() == 366 ? 53 : 52;
                };
                for (int p = 1; p <= maxPeriods; p++) {
                    String periodLabel = switch (periodType) {
                        case MONTH -> String.format("%d-%02d", year, p);
                        case WEEK -> String.format("%d-W%02d", year, p);
                    };
                    allPeriods.add(periodLabel);
                }
            }
        }

        // map pour savoir si une période a déjà eu des vraies données
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
                int maxPeriods = switch (periodType) {
                    case MONTH -> 12;
                    case WEEK -> Year.of(year).length() == 366 ? 53 : 52;
                };

                for (int p = 1; p <= maxPeriods; p++) {
                    String periodLabel = switch (periodType) {
                        case MONTH -> String.format("%d-%02d", year, p);
                        case WEEK -> String.format("%d-W%02d", year, p);
                    };

                    Double max = switch (periodType) {
                        case MONTH -> exerciseSetRepository.findMaxWeightByExerciseIdAndMonth(exerciseId, year, p);
                        case WEEK -> exerciseSetRepository.findMaxWeightByExerciseIdAndWeek(exerciseId, year, p);
                    };

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
            }
        }

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        WeekFields wf = WeekFields.ISO;
        int currentWeek = today.get(wf.weekOfWeekBasedYear());

        for (String period : allPeriods) {
            if (filledPeriods.contains(period)) continue;

            boolean isFuture = switch (periodType) {
                case MONTH -> {
                    String[] parts = period.split("-");
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    yield (year > currentYear) || (year == currentYear && month > currentMonth);
                }
                case WEEK -> {
                    String[] parts = period.split("-W");
                    int year = Integer.parseInt(parts[0]);
                    int week = Integer.parseInt(parts[1]);
                    yield (year > currentYear) || (year == currentYear && week > currentWeek);
                }
            };

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
        return getMainLiftsChartData(PeriodType.MONTH);
    }

    public List<ChartPeriodDataPointDTO> getMainLiftsChartDataWeekly() {
        return getMainLiftsChartData(PeriodType.WEEK);
    }
}
