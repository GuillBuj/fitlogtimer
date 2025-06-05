package com.fitlogtimer.util;

import com.fitlogtimer.constants.SuggestedExerciseColors;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("suggestedExerciseColors")
public class SuggestedExerciseColorUtil {

    public List<String> getColors15() {
        return SuggestedExerciseColors.COLORS_15;
    }

    public List<String> getColors30() {
        return SuggestedExerciseColors.COLORS_30;
    }

    public List<String> getColors() {
        return SuggestedExerciseColors.COLORS;
    }
}
