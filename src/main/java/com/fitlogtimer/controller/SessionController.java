package com.fitlogtimer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fitlogtimer.dto.SessionDetailsDTO;
import com.fitlogtimer.dto.SessionDetailsGroupedDTO;
import com.fitlogtimer.dto.SessionGroupedDTO;
import com.fitlogtimer.dto.SessionInDTO;
import com.fitlogtimer.dto.SessionOutDTO;
import com.fitlogtimer.model.Session;
import com.fitlogtimer.service.SessionService;

import lombok.extern.slf4j.Slf4j;



@Controller
@RequestMapping("/sessions")
@Slf4j
public class SessionController {

    @Autowired
    private SessionService sessionService;

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
        log.info("*/**/*/*/*/*/*/* Séance: {}", sessionData);
        model.addAttribute("sessionData", sessionData);
        log.info("Séance ajoutée au modèle : {}", model.getAttribute("session"));
        return "session-details";
    }

    @DeleteMapping("/{id}")
    public String deleteSession(@PathVariable int id, RedirectAttributes redirectAttributes) {
        boolean isDeleted = sessionService.deleteSession(id);
        if (isDeleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Séance supprimée avec succès !");
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
        model.addAttribute("session", new SessionInDTO(null, 0.0, ""));
        return "session-form";
    }

    @PostMapping("/create")
    public String createSession(@ModelAttribute("session") SessionInDTO sessionInDTO, RedirectAttributes redirectAttributes) {
        Session session = new Session();
        session.setDate(sessionInDTO.date());
        session.setBodyWeight(sessionInDTO.bodyWeight());
        session.setComment(sessionInDTO.comment());

        sessionService.saveSession(session);

        redirectAttributes.addFlashAttribute("successMessage", "Session créée avec succès !");
        return "redirect:/sessions/";
    }

}
