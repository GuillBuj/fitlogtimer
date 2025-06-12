package com.fitlogtimer.controller;

import com.fitlogtimer.service.WorkoutTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/workout-types")
@Slf4j
public class WorkoutTypeController {
    private final WorkoutTypeService workoutTypeService;

    @GetMapping
    public String getWorkoutTypeList(Model model) {
        model.addAttribute("workoutTypes", workoutTypeService.getAllWorkoutTypeItems());
        log.info("getWorkoutTypeList model:{}", model);
        return "workout-type-list";
    }

    @DeleteMapping("/{name}")
    public String deleteWorkout(@PathVariable String name, RedirectAttributes redirectAttributes) {
        boolean isDeleted = workoutTypeService.deleteWorkoutType(name);
        if (isDeleted) {
            redirectAttributes.addFlashAttribute("successRedMessage", "Séance type supprimée avec succès !");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Séance type non supprimée");
        }
        return "redirect:/workout-types";
    }
}
