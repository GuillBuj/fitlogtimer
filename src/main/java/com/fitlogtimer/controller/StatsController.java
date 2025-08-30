package com.fitlogtimer.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.fitlogtimer.constants.ExerciseSetType;
import com.fitlogtimer.dto.stats.*;
import org.springframework.beans.factory.annotation.Autowired;
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
public class StatsController {

    @Autowired
    private StatsService statsService;

    @Autowired
    private ExerciseService exerciseService;
    
    @GetMapping("/maxsByReps/{exerciseId}")
    public String showMaxsByExercise(@PathVariable int exerciseId, Model model){

        String exerciseName = exerciseService.getById(exerciseId).get().getName();

        MaxsByRepsDTO personalBests = statsService.mapFilteredMaxWeightsByReps(exerciseId);
        Map<Integer, MaxsByRepsDTO> bestsByYear = statsService.mapFilteredMaxWeightsByRepsForAllYears(exerciseId);

        List<CombinedMultiYearDTO> combinedMaxs = statsService.mergeMultiYearMaxsByReps(personalBests, bestsByYear);

        List<Integer> allYears = bestsByYear.keySet().stream().sorted(Comparator.reverseOrder()).toList();

        log.info("****************************************************** combinedMaxs: {} **************************************", combinedMaxs);

        model.addAttribute("exerciseName", exerciseName);
        model.addAttribute("combinedMaxs", combinedMaxs);
        model.addAttribute("exercise_id", exerciseId);
        model.addAttribute("allYears", allYears);

        log.info(model.toString());
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
