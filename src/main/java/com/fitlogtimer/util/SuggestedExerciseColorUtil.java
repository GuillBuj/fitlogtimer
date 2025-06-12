package com.fitlogtimer.util;

import com.fitlogtimer.constants.SuggestedColors;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("suggestedColors")
public class SuggestedExerciseColorUtil {

    public List<String> getLightColors() { return SuggestedColors.LIGHT_COLORS; }

    public List<String> getColors15() {
        return SuggestedColors.COLORS_15;
    }

    public List<String> getColors30() {
        return SuggestedColors.COLORS_30;
    }

    public List<String> getColors() {
        return SuggestedColors.COLORS;
    }
}
