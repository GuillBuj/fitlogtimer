package com.fitlogtimer.dto.preference;

import java.util.List;

public record AndroidExportDTO(
        List<JsonExerciseForAndroidDTO> exercises,
        List<String> workoutTypes
) {
}
