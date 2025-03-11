package com.fitlogtimer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitlogtimer.dto.SessionDetailsDTO;
import com.fitlogtimer.dto.SessionDetailsGroupedDTO;
import com.fitlogtimer.dto.SessionGroupedDTO;
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
        log.info("*/**/*/*/*/*/*/* Session: {}", sessionData);
        model.addAttribute("sessionData", sessionData); // Passer les données de la session à la vue
        log.info("Session ajoutée au modèle : {}", model.getAttribute("session"));
        return "session-details"; // Nom de la vue HTML (session-details.html)
    }

    // @GetMapping
    // public String listSessions(Model model) {
    //     List<Session> sessions = sessionService.getAllSessions();
    //     if (sessions.size()>0){
    //         log.info("-*-*-*-*-*-*-*-* Sessions: {}", sessions.toString());
    //     } else {
    //         log.info("-*-*-*-*-*-*-*-* Sessions: VIDE");
    //     }
        
    //     model.addAttribute("sessions", sessions);
    //     return "session-list";
    // }

}
