package com.fitlogtimer.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fitlogtimer.dto.ExerciseSetInDTO;
import com.fitlogtimer.dto.LastSetDTO;
import com.fitlogtimer.dto.SessionDetailsDTO;
import com.fitlogtimer.dto.SessionDetailsOutDTO;
import com.fitlogtimer.dto.SessionGroupedDTO;
import com.fitlogtimer.dto.SessionInDTO;
import com.fitlogtimer.dto.SessionOutDTO;
import com.fitlogtimer.dto.SetInSessionDTO;
import com.fitlogtimer.dto.SetInSessionOutDTO;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.Session;
import com.fitlogtimer.service.ExerciseService;
import com.fitlogtimer.service.SessionService;

import lombok.extern.slf4j.Slf4j;



@Controller
@RequestMapping("/sessions")
@Slf4j
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ExerciseService exerciseService;

    @PostMapping
    public SessionOutDTO createSession(@RequestBody Session session) {
        return sessionService.saveSession(session);
    }

    // @GetMapping("/id")
    // public Object getSessionDetailsDTO(@RequestParam int id, @RequestParam(defaultValue = "true") boolean grouped) {
    //     if (grouped) {
    //         return sessionService.getSessionGrouped(id);
    //     } else {
    //         return sessionService.getSessionDetails(id);
    //     }
    // }

    @GetMapping("/{id}")
    public String getSessionDetails(@PathVariable int id, Model model) {
        
        SessionGroupedDTO sessionData = sessionService.getSessionGrouped(id);
        LastSetDTO lastSet = sessionService.getLastSetDTO(id);
        ExerciseSetInDTO exerciseSet = new ExerciseSetInDTO(lastSet != null ? lastSet.exerciseId() : 1, lastSet != null ? lastSet.weight() : 0.0, lastSet != null ? lastSet.nbReps() : 0,false,"",id);
        List<Exercise> exercises=exerciseService.getAllExercises();

        model.addAttribute("sessionData", sessionData);
        model.addAttribute("exercises", exercises);
        model.addAttribute("exerciseSetDTO", exerciseSet);

        log.info(model.toString());
        return "session-details";
    }

    @GetMapping("/{id}/plus")
    public String getSessionDetailsPlus(@PathVariable int id, Model model) {
        
        SessionDetailsOutDTO sessionData = sessionService.getSessionDetailsBrut(id);
        LastSetDTO lastSet = sessionService.getLastSetDTO(id);
        ExerciseSetInDTO exerciseSet = new ExerciseSetInDTO(lastSet != null ? lastSet.exerciseId() : 1, lastSet != null ? lastSet.weight() : 0.0, lastSet != null ? lastSet.nbReps() : 0,false,"",id);
        List<Exercise> exercises=exerciseService.getAllExercises();
        
        
        model.addAttribute("sessionData", sessionData);
        model.addAttribute("exercises", exercises);
        model.addAttribute("exerciseSetDTO", exerciseSet);
        
        log.info(model.toString());
        return "session-details-plus";
    }

    @DeleteMapping("/{id}")
    public String deleteSession(@PathVariable int id, RedirectAttributes redirectAttributes) {
        boolean isDeleted = sessionService.deleteSession(id);
        if (isDeleted) {
            redirectAttributes.addFlashAttribute("successRedMessage", "Séance supprimée avec succès !");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Séance non supprimée");
        }
        return "redirect:/sessions/";
    }

    @GetMapping("/")
    public String getSessionsList(Model model) {
        List<Session> sessionsList = sessionService.getAllSessions();
        if (sessionsList.size()>0){
            log.info("-*-*-*-*-*-*-*-* Sessions: {}", sessionsList.toString());
        } else {
            log.info("-*-*-*-*-*-*-*-* Sessions: VIDE");
        }
        
        model.addAttribute("sessionsList", sessionsList);
        return "session-list";
    }
    
    

    @GetMapping("/create")
    public String showSessionForm(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("sessionData", new SessionInDTO(today, 0.0, ""));
        model.addAttribute("today", today);
        return "session-form";
    }

    @PostMapping("/create")
    public String createSession(@ModelAttribute("sessionData") SessionInDTO sessionInDTO, RedirectAttributes redirectAttributes) {
        log.info("POST sessions/create: {}", sessionInDTO);
        Session session = new Session();
        session.setDate(sessionInDTO.date());
        session.setBodyWeight(sessionInDTO.bodyWeight());
        session.setComment(sessionInDTO.comment());

        sessionService.saveSession(session);

        redirectAttributes.addFlashAttribute("successMessage", "Séance créée avec succès");
        return "redirect:/sessions/";
    }

}
