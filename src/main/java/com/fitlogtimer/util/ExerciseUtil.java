package com.fitlogtimer.util;

import com.fitlogtimer.constants.ExerciseSetType;
import com.fitlogtimer.enums.Family;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component("exerciseUtil")
public class ExerciseUtil {
    // ---- ExerciseSetType ----
    public String getDisplayName(String type) {
        return ExerciseSetType.DISPLAY_NAMES.getOrDefault(type, type);
    }
    public String getIconPath(String type) {
        return ExerciseSetType.ICON_PATHS.get(type);
    }

    // ---- Family ----
    public List<String> getFamilyOptions() {
        return Arrays.stream(Family.values())
                .filter(f -> f != Family.ALL)
                .map(Family::getCode)
                .toList();
    }

    public String getFamilyLabel(String code) {
        Family f = Family.fromCode(code);
        return f != null ? f.getLabel() : code;
    }

    public String getFamilyIcon(String code) {
        Family f = Family.fromCode(code);
        return f != null ? f.getIconPath() : null;
    }

    public boolean hasFamilyIcon(String code) {
        Family f = Family.fromCode(code);
        return f != null && f.hasIcon();
    }
}
