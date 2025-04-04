package com.fitlogtimer.controller;

import java.util.List;

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
@Slf4j
public class ExerciceSetController {
    @Autowired
    private ExerciseSetService exerciseSetService;

    @PostMapping("/add")
    public String addExerciseSet(@ModelAttribute ExerciseSetCreateDTO exerciseSetDTO, RedirectAttributes redirectAttributes) {
        try {
            exerciseSetService.saveExerciseSet(exerciseSetDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Série ajoutée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'ajout");
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
        return "redirect:/workouts/" + idWorkout + "/plus";
    }

    @GetMapping("/byExercise/{exerciseId}")
    public String showSetsByExercise(@PathVariable int exerciseId, Model model){
       
        List<ExerciseSet> sets = exerciseSetService.getSetsByExerciseId(exerciseId);
        model.addAttribute("sets", sets);

        log.info(model.toString());
        return "sets-by-exercise";
    }

    // @GetMapping("/byExercise/{exerciseId}/groupedByDate")
    // public String showSetsByExerciseGroupedByDate(@PathVariable int exerciseId, Model model){

    //     List<ExerciseSet> sets = exerciseSetService.getSetsByExerciseId(exerciseId);

    //     List<SetsGroupedForExDTO> groupedSets = exerciseSetService.groupSetsByWorkout(sets);

    //     model.addAttribute("sets", groupedSets);
    //     log.info(model.toString());
    //     return "sets-by-exercise-grouped";
    // }

    @GetMapping("/byExercise/{exerciseId}/groupedByDateClean")
    public String showSetsByExerciseGroupedCleanedByDate(@PathVariable int exerciseId, Model model){

        ExerciseDetailsGroupedDTO groupedSets = exerciseSetService.getSetsGroupedCleanedByWorkout(exerciseId);

        model.addAttribute("sets", groupedSets);
        log.info(model.toString());
        return "sets-by-exercise-grouped-cleaned";
    }

}

