package com.fitlogtimer.service;

import com.fitlogtimer.dto.chart.ChartDataPointDTO;
import com.fitlogtimer.dto.chart.ChartPeriodDataPointDTO;
import com.fitlogtimer.dto.details.ExerciseDetailsGroupedDTO;
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

    public List<ChartPeriodDataPointDTO> getMainLiftsChartDataMonthly() {
        List<String> mainShortNames = List.of("DC", "DC30", "DCS", "DL", "HSQ");

        List<Integer> exerciseIds = exerciseSetRepository.findIdsByShortNames(mainShortNames);
        List<ChartPeriodDataPointDTO> result = new ArrayList<>();

        for (Integer exerciseId : exerciseIds) {
            String exerciseName = exerciseRepository.findById(exerciseId)
                    .map(Exercise::getName)
                    .orElse("Exercice " + exerciseId);

            Integer firstYear = exerciseSetRepository.findFirstYearWithData(exerciseId);
            if (firstYear == null) continue;

            int currentYear = Year.now().getValue();
            for (int year = firstYear; year <= currentYear; year++) {
                for (int month = 1; month <= 12; month++) {
                    Double max = exerciseSetRepository.findMaxWeightByExerciseIdAndMonth(exerciseId, year, month);
                    if (max == null || max <= 0) continue;

                    result.add(new ChartPeriodDataPointDTO(
                            String.format("%d-%02d", year, month),
                            exerciseName,
                            max,
                            0.0  // 1RM estimé temporairement à 0
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

}
