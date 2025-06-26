package com.fitlogtimer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fitlogtimer.dto.stats.MaxsByRepsDTO;
import com.fitlogtimer.dto.stats.MaxsByRepsWithNameDTO;
import com.fitlogtimer.service.ExerciseService;
import com.fitlogtimer.service.StatsService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/stats")
@Slf4j
public class StatsController {

    @Autowired
    private StatsService statsService;

    @Autowired
    private ExerciseService exerciseService;
    
//    @GetMapping("/maxsByReps/{exerciseId}")
//    public String showMaxsByExercise(@PathVariable int exerciseId, Model model){
//
//        // MaxsByRepsDTO maxsByRepsDTO = statsService.mapMaxWeightsByReps(exerciseId, List.of(1,2,3,4,5,6,8,10,12, 15));
//        MaxsByRepsDTO maxsByRepsDTO = statsService.mapFilteredMaxWeightsByReps(exerciseId);
//        String exerciseName = exerciseService.getById(exerciseId).get().getName();
//
//        model.addAttribute("maxsWithName", new MaxsByRepsWithNameDTO(exerciseName, maxsByRepsDTO));
//        log.info(model.toString());
//        return "maxs-by-reps";
//    }
}
