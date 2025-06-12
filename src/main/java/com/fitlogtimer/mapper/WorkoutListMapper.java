package com.fitlogtimer.mapper;

import com.fitlogtimer.dto.listitem.WorkoutTypeListItemDTO;
import com.fitlogtimer.model.WorkoutType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkoutListMapper {

    WorkoutTypeListItemDTO toWorkoutTypeListItemDTO(WorkoutType workoutType);
}
