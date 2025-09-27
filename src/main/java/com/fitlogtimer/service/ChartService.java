package com.fitlogtimer.service;

import com.fitlogtimer.dto.chart.ChartDataPointDTO;
import com.fitlogtimer.dto.chart.ChartPeriodDataPointDTO;
import com.fitlogtimer.dto.details.ExerciseDetailsGroupedDTO;
import com.fitlogtimer.enums.PeriodType;
import com.fitlogtimer.mapper.ChartDataMapper;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.repository.ExerciseSetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.time.YearMonth;
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
        List<String> mainShortNames = List.of("DC", "DC30", "DCS", "DL", "HSQ");

        List<Integer> exerciseIds = exerciseSetRepository.findIdsByShortNames(mainShortNames);
        List<ChartPeriodDataPointDTO> result = new ArrayList<>();

        for (Integer exerciseId : exerciseIds) {
            String exerciseName = exerciseRepository.findById(exerciseId)
                    .map(Exercise::getName)
                    .orElse("Exercice " + exerciseId);

            Integer firstYear = exerciseSetRepository.findFirstYearWithData(exerciseId);
            Integer lastYear = exerciseSetRepository.findLastYearWithData(exerciseId);
            if (firstYear == null || lastYear == null) continue;

            for (int year = firstYear; year <= lastYear; year++) {
                int maxPeriods = switch (periodType) {
                    case MONTH -> 12;
                    case WEEK -> Year.of(year).length() == 366 ? 53 : 52;
                };

                for (int p = 1; p <= maxPeriods; p++) {
                    Double max = switch (periodType) {
                        case MONTH -> exerciseSetRepository.findMaxWeightByExerciseIdAndMonth(exerciseId, year, p);
                        case WEEK -> exerciseSetRepository.findMaxWeightByExerciseIdAndWeek(exerciseId, year, p);
                    };

                    //if (max == null || max <= 0) continue;

                    String periodLabel = switch (periodType) {
                        case MONTH -> String.format("%d-%02d", year, p);
                        case WEEK -> String.format("%d-W%02d", year, p);
                    };

                    result.add(new ChartPeriodDataPointDTO(
                            periodLabel,
                            exerciseName,
                            max,
                            0.0 // 1RM estimé temporairement à 0
                    ));
                }
            }
        }

        return result.stream()
                .sorted(Comparator
                        .comparing(ChartPeriodDataPointDTO::period)
                        .thenComparing(ChartPeriodDataPointDTO::exerciseName))
                .toList();
    }

    public List<ChartPeriodDataPointDTO> getMainLiftsChartDataMonthly() {
        return getMainLiftsChartData(PeriodType.MONTH);
    }

    public List<ChartPeriodDataPointDTO> getMainLiftsChartDataWeekly() {
        return getMainLiftsChartData(PeriodType.WEEK);
    }

}
