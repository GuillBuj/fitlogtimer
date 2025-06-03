package com.fitlogtimer.util;

import com.fitlogtimer.constants.ExerciseColorConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ExerciseColorUtil {

    public String getDisplayColor(String colorFromEntity, String shortName) {
        if (StringUtils.hasText(colorFromEntity)){
            return colorFromEntity;
        }

        String fallback= ExerciseColorConstants.getColorForExercise(shortName);
        return (fallback != null) ? fallback : "#CCCCCC";
    }
}
