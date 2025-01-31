package com.fitlogtimer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.service.ExerciseService;

@RestController
@RequestMapping("/api/exercise")
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    @PostMapping
    public Exercise createExercise(@RequestBody Exercise exercise) {
        return exerciseService.saveExercise(exercise);
    }
}
