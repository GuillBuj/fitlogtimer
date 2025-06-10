package com.fitlogtimer.util;

import com.fitlogtimer.constants.ExerciseColorConstants;
import com.fitlogtimer.service.ExerciseService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExerciseColorUtil {

    private final ExerciseService exerciseService;
    private Map<String,String> colorCache; //pas final car utilisé dans @PostConstruct

    @PostConstruct
    public void init() {
        this.colorCache = exerciseService.getAllExerciseColors();
    }

    public String getDisplayColor(String colorFromEntity, String shortName) {
        //log.info("getDisplayColor({}, {})", colorFromEntity, shortName);
        if (StringUtils.hasText(colorFromEntity)){
            return colorFromEntity;
        }

        String fallback= ExerciseColorConstants.getColorForExercise(shortName);
        return (fallback != null) ? fallback : "#CCCCCC";
    }

    public String getDisplayColor(String shortName) {
        //log.info("getDisplayColor({}): {}", shortName, colorCache.getOrDefault(shortName, "default"));
        return colorCache.getOrDefault(shortName, "#666666");
    }
}
