package com.fitlogtimer.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fitlogtimer.constants.ExerciseSetType;
import com.fitlogtimer.dto.ExerciseSetWithBodyWeightAndDateDTO;
import com.fitlogtimer.dto.ExerciseSetWithBodyWeightAndDateFor1RMDTO;
import com.fitlogtimer.dto.YearlyBestRatioFor1RMWithTrendDTO;
import com.fitlogtimer.dto.YearlyBestRatioWithTrendDTO;
import com.fitlogtimer.dto.stats.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fitlogtimer.service.ExerciseService;
import com.fitlogtimer.service.StatsService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/stats")
@Slf4j
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    private final ExerciseService exerciseService;

    @GetMapping("/maxsByReps/{exerciseId}")
    public String showMaxsByExercise(@PathVariable int exerciseId, Model model) {
        String exerciseName = exerciseService.getById(exerciseId).get().getName();
        String exerciseType = exerciseService.getById(exerciseId).get().getType();

        MaxsByRepsDTO personalBests = statsService.mapFilteredMaxWeightsByReps(exerciseId);
        Map<Integer, MaxsByRepsDTO> bestsByYear = statsService.mapFilteredMaxWeightsByRepsForAllYears(exerciseId);
        List<CombinedMultiYearDTO> combinedMaxs = statsService.mergeMultiYearMaxsByReps(personalBests, bestsByYear);

        ExerciseSetWithBodyWeightAndDateDTO maxRatioSet = statsService.getTopMaxRatioSet(exerciseId);
        Map<Integer, YearlyBestRatioWithTrendDTO> yearlyMaxRatioSets = statsService.getTopMaxRatioSetByYears(exerciseId);
        ExerciseSetWithBodyWeightAndDateFor1RMDTO maxRatio1RMSet = statsService.getTop1RMRatioSet(exerciseId);
        Map<Integer, YearlyBestRatioFor1RMWithTrendDTO> yearlyMaxRatio1RMSets = statsService.getTop1RMRatioSetByYears(exerciseId);

        List<Integer> allYears = bestsByYear.keySet().stream().sorted(Comparator.reverseOrder()).toList();

        log.info("*** combinedMaxs: {}", combinedMaxs);
        log.info("*** yearlyMaxRatio1RMSets: {}", yearlyMaxRatio1RMSets);

        if (!Objects.equals(exerciseType, ExerciseSetType.FREE_WEIGHT)) {
            Map<Integer, MaxWithDateDTO> yearlyMaxRecords = statsService.getYearlyMaxRecords(exerciseId, exerciseType);
            log.info("*** yearlyMaxRecords: {}", yearlyMaxRecords);
            CombinedSimpleMultiYearDTO simpleMaxs = statsService.mergeSimpleMultiYearMaxs(yearlyMaxRecords);
            log.info("*** simpleMaxs: {}", simpleMaxs);
            model.addAttribute("simpleMaxs", simpleMaxs);
        }

        model.addAttribute("exerciseName", exerciseName);
        model.addAttribute("combinedMaxs", combinedMaxs);
        model.addAttribute("maxRatioSet", maxRatioSet);
        model.addAttribute("yearlyMaxRatioSets", yearlyMaxRatioSets);
        model.addAttribute("maxRatio1RMSet", maxRatio1RMSet);
        model.addAttribute("yearlyMaxRatio1RMSets", yearlyMaxRatio1RMSets);
        model.addAttribute("allYears", allYears);
        model.addAttribute("exercise_id", exerciseId);
        model.addAttribute("exercise_type", exerciseType);

        return "maxs-by-reps";
    }

    @GetMapping("/recordHistory/{exerciseId}")
    public String showRecordHistory(@PathVariable int exerciseId, Model model){

        String exerciseName = exerciseService.getById(exerciseId).get().getName();
        String exerciseType = exerciseService.getById(exerciseId).get().getType();

        List<RecordHistoryItem> recordHistory = statsService.getMinimalRecordHistory(statsService.getRecordHistory(exerciseId));

        model.addAttribute("exerciseName", exerciseName);
        model.addAttribute("recordHistory", recordHistory);
        model.addAttribute("exercise_id", exerciseId);
        model.addAttribute("exercise_type", exerciseType);
        model.addAttribute("FREE_WEIGHT_TYPE", ExerciseSetType.FREE_WEIGHT);
        model.addAttribute("ISOMETRIC_TYPE", ExerciseSetType.ISOMETRIC);
        model.addAttribute("BODYWEIGHT_TYPE", ExerciseSetType.BODYWEIGHT);

        log.info(model.toString());
        return "record-history";
    }

    @GetMapping("/recordHistoryPlus/{exerciseId}")
    public String showMinimalRecordHistory(@PathVariable int exerciseId, Model model){

        String exerciseName = exerciseService.getById(exerciseId).get().getName();
        String exerciseType = exerciseService.getById(exerciseId).get().getType();

        List<RecordHistoryItem> recordHistory = statsService.getRecordHistory(exerciseId);

        model.addAttribute("exerciseName", exerciseName);
        model.addAttribute("recordHistory", recordHistory);
        model.addAttribute("exercise_id", exerciseId);
        model.addAttribute("exercise_type", exerciseType);
        model.addAttribute("FREE_WEIGHT_TYPE", ExerciseSetType.FREE_WEIGHT);
        model.addAttribute("ISOMETRIC_TYPE", ExerciseSetType.ISOMETRIC);
        model.addAttribute("BODYWEIGHT_TYPE", ExerciseSetType.BODYWEIGHT);

        log.info(model.toString());
        return "record-history";
    }

}
