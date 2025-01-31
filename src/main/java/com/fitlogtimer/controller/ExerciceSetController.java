package com.fitlogtimer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.service.ExerciseSetService;

@RestController
@RequestMapping("/api/exerciseSet")
public class ExerciceSetController {
    @Autowired
    private ExerciseSetService exerciceSetService;

    @PostMapping
    public ExerciseSet createExerciseSet(@RequestBody ExerciseSet exerciseSet) {
        return exerciceSetService.saveExerciseSet(exerciseSet);
    }
}
