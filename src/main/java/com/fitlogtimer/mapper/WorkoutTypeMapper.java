package com.fitlogtimer.mapper;

import com.fitlogtimer.dto.listitem.WorkoutTypeListItemDTO;
import com.fitlogtimer.dto.update.WorkoutTypeUpdateDTO;
import com.fitlogtimer.model.WorkoutType;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface WorkoutTypeMapper {

    WorkoutTypeListItemDTO toWorkoutTypeListItemDTO(WorkoutType workoutType);

    WorkoutTypeUpdateDTO toWorkoutTypeUpdateDTO(WorkoutType workoutType);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "name", ignore = true) // on ignore le nom pour ne pas le modifier
    void updateWorkoutTypeFromDTO(WorkoutTypeUpdateDTO dto, @MappingTarget WorkoutType workoutType);
}
