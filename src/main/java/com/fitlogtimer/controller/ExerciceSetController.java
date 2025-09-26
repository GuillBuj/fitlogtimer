package com.fitlogtimer.controller;

import java.util.List;
import java.util.Set;

import com.fitlogtimer.dto.chart.ChartDataPoint;
import com.fitlogtimer.service.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.dto.details.ExerciseDetailsGroupedDTO;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.service.ExerciseSetService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/exerciseSets")
@RequiredArgsConstructor
@Slf4j
public class ExerciceSetController {

    private final ExerciseSetService exerciseSetService;

    private final ChartService chartService;

    @PostMapping("/add")
    public String addExerciseSet(@ModelAttribute ExerciseSetCreateDTO exerciseSetDTO, RedirectAttributes redirectAttributes) {
        try {
            exerciseSetService.saveExerciseSet(exerciseSetDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Série ajoutée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'ajout:"+ e);
        }
        return "redirect:/workouts/" + exerciseSetDTO.workout_id();
    }

    @DeleteMapping("/{id}")
    public String deleteExerciseSet(@PathVariable int id, @RequestParam int idWorkout, RedirectAttributes redirectAttributes){
        boolean isDeleted = exerciseSetService.deleteExerciseSet(id);
        if (isDeleted) {
            redirectAttributes.addFlashAttribute("successRedMessage", "Série supprimée avec succès");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Série non supprimée");
        }
        return "redirect:/workouts/" + idWorkout + "/brut";
    }

    @GetMapping("/byExercise/{exerciseId}")
    public String showSetsByExercise(@PathVariable int exerciseId, Model model){
       
        List<ExerciseSet> sets = exerciseSetService.getSetsByExerciseId(exerciseId);
        model.addAttribute("sets", sets);

        log.info(model.toString());
        return "sets-by-exercise";
    }

    @GetMapping("/byExercise/{exerciseId}/groupedByDateClean")
    public String showSetsByExerciseGroupedCleanedByDate(@PathVariable int exerciseId,
                                                         @RequestParam(required = false) Set<String> selectedTypes,
                                                         Model model){

        ExerciseDetailsGroupedDTO groupedSets = exerciseSetService.getSetsGroupedCleanedByWorkout(exerciseId, selectedTypes);
        Set<String> allTypes = exerciseSetService.extractTypes(groupedSets);
        List<ChartDataPoint> chartData = chartService.getProgressChartData(groupedSets);

        model.addAttribute("sets", groupedSets);
        model.addAttribute("types", allTypes);
        model.addAttribute("exercise_id", exerciseId);
        model.addAttribute("chartData", chartData);
        log.info(chartData.toString());
        return "sets-by-exercise-grouped-cleaned";
    }

    @GetMapping("/byExercise/{exerciseId}/groupedByDateClean/fragment")
    public String showSetsByExerciseGroupedCleanedByDateGetFragmentOnly(@PathVariable int exerciseId,
                                @RequestParam(required = false) Set<String> selectedTypes,
                                Model model) {

        ExerciseDetailsGroupedDTO groupedSets = exerciseSetService.getSetsGroupedCleanedByWorkout(exerciseId, selectedTypes);
        model.addAttribute("sets", groupedSets);

        return "fragments/sets-by-exercise-cleaned-filtered :: exerciseSets(sets=${sets})";
    }

}

