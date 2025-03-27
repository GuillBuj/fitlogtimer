package com.fitlogtimer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitlogtimer.service.DBService;

@RestController
@RequestMapping("api/db")
public class DBController {
    @Autowired
    private DBService dbService;

    @DeleteMapping
    void clearExerciseSetAndWorkout(){
        dbService.clearExerciseSetAndWorkout();
    }
}
