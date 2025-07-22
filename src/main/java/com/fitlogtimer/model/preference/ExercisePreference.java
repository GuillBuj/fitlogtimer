package com.fitlogtimer.model.preference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExercisePreference {
    private int exerciseId;
    private int order;
    private boolean visible;
}
