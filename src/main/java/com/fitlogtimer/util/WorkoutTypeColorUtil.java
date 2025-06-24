package com.fitlogtimer.util;

import com.fitlogtimer.constants.ExerciseColorConstants;
import com.fitlogtimer.service.ExerciseService;
import com.fitlogtimer.service.WorkoutTypeService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkoutTypeColorUtil {

    private final WorkoutTypeService workoutTypeService;
    private Map<String,String> colorCache; //pas final car utilisé dans @PostConstruct

    @PostConstruct
    public void init() {
        this.colorCache = workoutTypeService.getAllExerciseColors();
    }

    public String getDisplayColor(String colorFromEntity, String shortName) {
        if (StringUtils.hasText(colorFromEntity)){
            return colorFromEntity;
        }

        String fallback= ExerciseColorConstants.getColorForExercise(shortName);
        return (fallback != null) ? fallback : "#CCCCCC";
    }

    public String getDisplayColor(String shortName) {
        return colorCache.getOrDefault(shortName, "#666666");
    }
}
