package com.fitlogtimer.controller;

import com.fitlogtimer.dto.stats.ExerciseStatCountBasicDTO;
import com.fitlogtimer.dto.stats.ExerciseStatCountWeightDTO;
import com.fitlogtimer.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/count")
@RequiredArgsConstructor
@Slf4j
public class CountController {

    private final StatsService statsService;

    @GetMapping("/all")
    public String showBasicCountForAllExercises(Model model) throws IOException {

        List<ExerciseStatCountBasicDTO> exerciseCountList = statsService.getBasicCountsForAllExercises();

        model.addAttribute("exerciseCountsList", exerciseCountList);

        return "counts-basic-list";
    }

    @GetMapping
    public String showBasicCountForWeightExercises(Model model) throws IOException {

        List<ExerciseStatCountWeightDTO> exerciseCountList = statsService.getBasicCountsForWeightExercises();

        model.addAttribute("exerciseCountsList", exerciseCountList);
        model.addAttribute("type", "WEIGHT");

        return "counts-basic-list";
    }
}
