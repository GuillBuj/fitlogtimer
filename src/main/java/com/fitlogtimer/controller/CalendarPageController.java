package com.fitlogtimer.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class CalendarPageController {

    @GetMapping("/calendar")
    public String getCalendarPage(Model model) {
        return "calendar"; // Cette vue doit correspondre à un fichier calendar.html
    }
}

