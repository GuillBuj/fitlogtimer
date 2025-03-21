package com.fitlogtimer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fitlogtimer.dto.ExerciseCreateDTO;
import com.fitlogtimer.enums.Family;
import com.fitlogtimer.enums.Muscle;
import com.fitlogtimer.service.ExerciseService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/exercises")
@Slf4j
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    @GetMapping
    public String showExercisesList(Model model){
        model.addAttribute("exercises", exerciseService.getAllExercises());
        model.addAttribute("exercise", new ExerciseCreateDTO("", "", Muscle.ALL, Family.ALL));
        model.addAttribute("muscles", Muscle.values());
        model.addAttribute("families", Family.values());
        return "exercises-list";
    }
    
    // @GetMapping("/create")
    // public String createExercise(Model model) {
    //     model.addAttribute("exercise", new ExerciseCreateDTO("", "", Muscle.ALL, Family.ALL));
    //     model.addAttribute("muscles", Muscle.values());
    //     model.addAttribute("families", Family.values());
    //     return "exercise-create";
    // }

    @PostMapping("/create")
    public String createExercise(@ModelAttribute("exercise") ExerciseCreateDTO exerciseCreateDTO, RedirectAttributes redirectAttributes){
        exerciseService.createExercise(exerciseCreateDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Séance créée avec succès");
        return "redirect:/exercises";
    }

    @DeleteMapping("/{id}")
    public String deleteExercise(@PathVariable int id, RedirectAttributes redirectAttributes){
        log.info("DELETE {}", id);
        boolean isDeleted = exerciseService.deleteExerciseSet(id);
        if (isDeleted) {
            redirectAttributes.addFlashAttribute("successRedMessage", "Exercice supprimé avec succès");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Exercice non supprimé");
        }
        return "redirect:/exercises";
    }
 
}
