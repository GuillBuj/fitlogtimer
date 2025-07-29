package com.fitlogtimer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TimerController {

    @GetMapping("/timer")
    public String showTimerPage(Model model) {
        return "timer";
    }
}
