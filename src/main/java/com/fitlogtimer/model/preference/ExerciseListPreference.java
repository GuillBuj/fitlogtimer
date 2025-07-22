package com.fitlogtimer.model.preference;

import com.fitlogtimer.model.Exercise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseListPreference {
    private String name;
    private List<ExercisePreference> exercises;
}
