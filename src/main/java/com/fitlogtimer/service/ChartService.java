package com.fitlogtimer.service;

import com.fitlogtimer.dto.chart.ChartDataPointDTO;
import com.fitlogtimer.dto.details.ExerciseDetailsGroupedDTO;
import com.fitlogtimer.mapper.ChartDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChartService {

    private final ChartDataMapper chartDataMapper;

    public List<ChartDataPointDTO> getProgressChartData(ExerciseDetailsGroupedDTO exerciseDetails) {
        if (exerciseDetails == null || exerciseDetails.exerciseSets() == null) {
            return Collections.emptyList();
        }

        return chartDataMapper.toChartDataPoints(exerciseDetails.exerciseSets());
    }
}
