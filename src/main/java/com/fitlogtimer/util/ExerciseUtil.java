package com.fitlogtimer.util;

import com.fitlogtimer.constants.ExerciseSetType;
import org.springframework.stereotype.Component;

@Component("exerciseUtil")
public class ExerciseUtil {
    public String getDisplayName(String type) {
        return ExerciseSetType.DISPLAY_NAMES.getOrDefault(type, type);
    }
}
