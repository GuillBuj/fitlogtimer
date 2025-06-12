package com.fitlogtimer.controller;

import com.fitlogtimer.dto.update.WorkoutTypeUpdateDTO;
import com.fitlogtimer.service.WorkoutTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/{name}/edit")
    public String showUpdateForm(@PathVariable String name, Model model, RedirectAttributes redirectAttributes) {
        try {
            WorkoutTypeUpdateDTO dto = workoutTypeService.getWorkoutTypeUpdateDTO(name);
            model.addAttribute("workoutTypeUpdateDTO", dto);
            return "workout-type-edit";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "WorkoutType introuvable : " + name);
            return "redirect:/workout-types";
        }
    }

    @PostMapping("/{name}/edit")
    public String updateWorkoutType(
            @PathVariable String name,
            @ModelAttribute("workoutTypeUpdateDTO") @Valid WorkoutTypeUpdateDTO dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "workout-type-edit";
        }

        try {
            workoutTypeService.updateWorkoutType(dto);
            redirectAttributes.addFlashAttribute("success", "WorkoutType mis à jour avec succès.");
            return "redirect:/workout-types";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/workout-types/" + name + "/edit";
        }
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
