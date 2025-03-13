package com.fitlogtimer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fitlogtimer.dto.ExerciseSetInDTO;
import com.fitlogtimer.service.ExerciseSetService;

@Controller
@RequestMapping("/exerciseSets")
public class ExerciceSetController {
    @Autowired
    private ExerciseSetService exerciseSetService;

    @PostMapping("/add")
    public String addExerciseSet(@ModelAttribute ExerciseSetInDTO exerciseSetDTO, RedirectAttributes redirectAttributes) {
        try {
            exerciseSetService.saveExerciseSet(exerciseSetDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Exercice ajouté avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'ajout");
        }
        return "redirect:/sessions/" + exerciseSetDTO.session_id();
    }

    @DeleteMapping("/{id}")
    public String deleteExerciseSet(@PathVariable int id, @RequestParam int idSession, RedirectAttributes redirectAttributes){
        boolean isDeleted = exerciseSetService.deleteExerciseSet(id);
        if (isDeleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Série supprimée avec succès");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Série non supprimée");
        }
        return "redirect:/sessions/" + idSession + "/plus";
    }


}
