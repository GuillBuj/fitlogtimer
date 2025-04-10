package com.fitlogtimer.dto.display;

import java.util.List;
import java.util.Map;

import com.fitlogtimer.dto.listitem.CalendarItemDTO;

public record CalendarDTO(
    List<CalendarItemDTO> calendarItems,
    Map<String, String> exerciseColors,
    Map<String, String> workoutTypeColors
) {

}
