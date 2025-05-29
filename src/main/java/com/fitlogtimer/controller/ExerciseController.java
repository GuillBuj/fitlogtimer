package com.fitlogtimer.controller;

import com.fitlogtimer.dto.update.ExerciseUpdateDTO;
import com.fitlogtimer.model.Exercise;
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

import com.fitlogtimer.constants.ExerciseSetType;
import com.fitlogtimer.dto.create.ExerciseCreateDTO;
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
        
        model.addAttribute("exercises", exerciseService.getAllExerciseItems());
        model.addAttribute("exercise", new ExerciseCreateDTO("", "", Muscle.ALL, Family.ALL, ExerciseSetType.FREE_WEIGHT));
        model.addAttribute("muscles", Muscle.values());
        model.addAttribute("families", Family.values());
        model.addAttribute("setTypes", ExerciseSetType.DISPLAY_NAMES);
        model.addAttribute("FREE_WEIGHT_TYPE", ExerciseSetType.FREE_WEIGHT);
        model.addAttribute("MOVEMENT_TYPE", ExerciseSetType.MOVEMENT);
        model.addAttribute("ELASTIC_TYPE", ExerciseSetType.ELASTIC);
        model.addAttribute("ISOMETRIC_TYPE", ExerciseSetType.ISOMETRIC);

        return "exercises-list";
    }
    
    @PostMapping("/create")
    public String createExercise(@ModelAttribute("exercise") ExerciseCreateDTO exerciseCreateDTO, RedirectAttributes redirectAttributes){
        
        exerciseService.createExercise(exerciseCreateDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Exercice créé avec succès");
        
        return "redirect:/exercises";
    }

    @GetMapping("/editForm/{id}")
    public String getEditForm(@PathVariable int id, Model model) {
        ExerciseUpdateDTO dto = exerciseService.getExerciseUpdateDTO(id);
        model.addAttribute("exercise", dto);
        model.addAttribute("muscles", Muscle.values());
        model.addAttribute("families", Family.values());
        model.addAttribute("setTypes", ExerciseSetType.DISPLAY_NAMES);
        return "fragments/exercise-row-edit:: editRow";
    }

    @GetMapping("/viewRow/{id}")
    public String getExerciseRow(@PathVariable int id, Model model) {
        model.addAttribute("exercise", exerciseService.findById(id));
        model.addAttribute("FREE_WEIGHT_TYPE", ExerciseSetType.FREE_WEIGHT);
        model.addAttribute("MOVEMENT_TYPE", ExerciseSetType.MOVEMENT);
        model.addAttribute("ELASTIC_TYPE", ExerciseSetType.ELASTIC);
        model.addAttribute("ISOMETRIC_TYPE", ExerciseSetType.ISOMETRIC);
        return "fragments/exercise-row :: row";
    }

    @PostMapping("/update")
    public String updateExercise(@ModelAttribute ExerciseUpdateDTO dto, Model model) {
        exerciseService.updateExercise(dto);
        model.addAttribute("exercise", exerciseService.findById(dto.id()));
        model.addAttribute("FREE_WEIGHT_TYPE", ExerciseSetType.FREE_WEIGHT);
        model.addAttribute("MOVEMENT_TYPE", ExerciseSetType.MOVEMENT);
        model.addAttribute("ELASTIC_TYPE", ExerciseSetType.ELASTIC);
        model.addAttribute("ISOMETRIC_TYPE", ExerciseSetType.ISOMETRIC);
        return "fragments/exercise-row :: row";
    }

    @GetMapping("/exercises/cancel/{id}")
    public String cancelEdit(@PathVariable int id, Model model) {
        model.addAttribute("exercise", exerciseService.findById(id));
        return "exercises/list :: #specific-row-" + id;
    }



    @DeleteMapping("/{id}")
    public String deleteExercise(@PathVariable int id, RedirectAttributes redirectAttributes){
        log.info("DELETE /exercises/{}", id);
        
        boolean isDeleted = exerciseService.deleteExerciseSet(id);
        if (isDeleted) {
            redirectAttributes.addFlashAttribute("successRedMessage", "Exercice supprimé avec succès");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Exercice non supprimé");
        }

        return "redirect:/exercises";
    }
 
}
