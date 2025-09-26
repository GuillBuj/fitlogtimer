package com.fitlogtimer.mapper;

import com.fitlogtimer.dto.chart.ChartDataPoint;
import com.fitlogtimer.dto.listitem.SetGroupCleanExerciseListItemDTO;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ChartDataMapper {

    ChartDataPoint toChartDataPoint(SetGroupCleanExerciseListItemDTO setGroup);

    default List<ChartDataPoint> toChartDataPoints(List<SetGroupCleanExerciseListItemDTO> setGroups) {
        if (setGroups == null) {
            return Collections.emptyList();
        }

        return setGroups.stream()
                .map(this::toChartDataPoint)
                .sorted(Comparator.comparing(ChartDataPoint::date))
                .collect(Collectors.toList());
    }
}
