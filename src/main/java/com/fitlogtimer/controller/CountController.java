package com.fitlogtimer.controller;

import com.fitlogtimer.dto.stats.ExerciseStatCountBasicDTO;
import com.fitlogtimer.dto.stats.ExerciseStatCountWeightDTO;
import com.fitlogtimer.dto.stats.ExerciseStatCountWeightYearlyDTO;
import com.fitlogtimer.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @GetMapping("/yearly")
    public String showWeightExercisesStats(Model model) throws IOException {
        List<ExerciseStatCountWeightYearlyDTO> stats = statsService.getYearlyBasicCountsForWeightExercises();

        List<String> years = stats.stream()
                .filter(dto -> !dto.statsByYear().isEmpty())
                .findFirst()
                .map(dto -> new ArrayList<>(dto.statsByYear().keySet()))
                .orElse(new ArrayList<>());

        model.addAttribute("stats", stats);
        model.addAttribute("years", years);

        return "counts-yearly-list";
    }
}
