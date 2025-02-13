package com.fitlogtimer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitlogtimer.dto.SessionDetailsDTO;
import com.fitlogtimer.dto.SessionDetailsGroupedDTO;
import com.fitlogtimer.dto.SessionOutDTO;
import com.fitlogtimer.model.Session;
import com.fitlogtimer.service.SessionService;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @PostMapping
    public SessionOutDTO createSession(@RequestBody Session session) {
        return sessionService.saveSession(session);
    }

    @GetMapping
    public Object getSessionDetailsDTO(@RequestParam int id, @RequestParam(defaultValue = "true") boolean grouped) {
        if (grouped) {
            return sessionService.getSessionGrouped(id);
        } else {
            return sessionService.getSessionDetails(id);
        }
    }

}
