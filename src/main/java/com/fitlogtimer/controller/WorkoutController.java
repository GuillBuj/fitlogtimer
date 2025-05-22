package com.fitlogtimer.controller;

import java.time.LocalDate;
import java.util.List;

import com.fitlogtimer.dto.update.WorkoutUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.dto.create.WorkoutCreateDTO;
import com.fitlogtimer.dto.details.WorkoutDetailsBrutDTO;
import com.fitlogtimer.dto.details.WorkoutDetailsGroupedDTO;
import com.fitlogtimer.dto.display.WorkoutListDisplayDTO;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.service.ExerciseService;
import com.fitlogtimer.service.WorkoutService;

import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping("/workouts")
@Slf4j
public class WorkoutController {

    @Autowired
    private WorkoutService workoutService;

    @Autowired
    private ExerciseService exerciseService;

    @GetMapping("/{id}")
    public String getWorkoutDetails(@PathVariable int id, Model model) {
        
        WorkoutDetailsGroupedDTO workoutData = workoutService.getWorkoutGrouped(id);
        ExerciseSetCreateDTO exerciseSet = workoutService.setFormByLastSetDTO(id);
        List<Exercise> exercises = exerciseService.getAllExercises();

        model.addAttribute("workoutData", workoutData);
        model.addAttribute("exercises", exercises);
        model.addAttribute("exerciseSetDTO", exerciseSet);

        log.info(model.toString());
        return "workout-details";
    }

    @GetMapping("/{id}/brut")
    public String getWorkoutDetailsPlus(@PathVariable int id, Model model) {
        
        WorkoutDetailsBrutDTO workoutData = workoutService.getWorkoutDetailsBrut(id);
        ExerciseSetCreateDTO exerciseSet = workoutService.setFormByLastSetDTO(id);
        List<Exercise> exercises=exerciseService.getAllExercises();
        
        model.addAttribute("workoutData", workoutData);
        model.addAttribute("exercises", exercises);
        model.addAttribute("exerciseSetDTO", exerciseSet);
        
        log.info(model.toString());
        return "workout-details-brut";
    }

    @GetMapping("/{id}/edit-infos")
    public String editWorkoutInfos(@PathVariable int id, Model model) {
        WorkoutUpdateDTO workout = workoutService.getWorkoutUpdateDTO(id);
        model.addAttribute("workoutUpdateDTO", workout);
        return "fragments/workout-edit :: workout-edit";
    }

    @GetMapping("/{id}/infos")
    public String displayWorkoutInfos(@PathVariable int id, Model model) {
        WorkoutUpdateDTO workout = workoutService.getWorkoutUpdateDTO(id);
        model.addAttribute("workoutData", workout);
        return "fragments/workout-display :: workout-display";
    }

    @PutMapping("/{id}")
    public String updateWorkout(
            @PathVariable int id,
            @ModelAttribute WorkoutUpdateDTO workoutUpdateDTO,
            Model model
    ) {
        workoutService.updateWorkout(workoutUpdateDTO);
        WorkoutUpdateDTO updated = workoutService.getWorkoutUpdateDTO(id);
        model.addAttribute("workoutData", updated);
        return "fragments/workout-display :: workout-display";
    }

    @DeleteMapping("/{id}")
    public String deleteWorkout(@PathVariable int id, RedirectAttributes redirectAttributes) {
        boolean isDeleted = workoutService.deleteWorkout(id);
        if (isDeleted) {
            redirectAttributes.addFlashAttribute("successRedMessage", "Séance supprimée avec succès !");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Séance non supprimée");
        }
        return "redirect:/workouts";
    }

    // @GetMapping
    // public String getWorkoutsList(Model model) {
    //     List<WorkoutListDisplayDTO> workoutList = workoutService.getAllWorkoutsDisplayDTO();
    //     if (workoutList.size()>0){
    //         log.info("-*-*-*-*-*-*-*-* Workouts: {}", workoutList.toString());
    //     } else {
    //         log.info("-*-*-*-*-*-*-*-* Workouts: VIDE");
    //     }
        
    //     model.addAttribute("workoutList", workoutList);
    //     return "workout-list";
    // }

    @GetMapping
    public String getWorkoutsList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<WorkoutListDisplayDTO> workoutPage = workoutService.getPaginatedWorkoutsDisplayDTO(page, size);
        // if (workoutList.size()>0){
        //     log.info("-*-*-*-*-*-*-*-* Workouts: {}", workoutList.toString());
        // } else {
        //     log.info("-*-*-*-*-*-*-*-* Workouts: VIDE");
        // }
        
        model.addAttribute("workoutPage", workoutPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", workoutPage.getTotalPages());
        model.addAttribute("workoutList", workoutPage.getContent());
        return "workout-list";
    }
    
    @GetMapping("/create")
    public String showWorkoutForm(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("workoutData", new WorkoutCreateDTO(today, 0.0, "", ""));
        model.addAttribute("today", today);
        return "workout-create";
    }

    @PostMapping("/create")
    public String createWorkout(@ModelAttribute("workoutData") WorkoutCreateDTO workoutCreateDTO, RedirectAttributes redirectAttributes) {
        log.info("POST workouts/create: {}", workoutCreateDTO);
        
        workoutService.createWorkout(workoutCreateDTO);

        redirectAttributes.addFlashAttribute("successMessage", "Séance créée avec succès");
        return "redirect:/workouts";
    }

}
