package com.fitlogtimer.controller;

import com.fitlogtimer.dto.preference.ExercisePreferenceDTO;
import com.fitlogtimer.model.preference.ExerciseListPreference;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.service.ExercisePreferenceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/preferences")
@AllArgsConstructor
public class PreferenceController {

    private final ExercisePreferenceService preferenceService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<ExercisePreferenceDTO> dtoList = preferenceService.getDefaultPreferenceDTOs();
        model.addAttribute("exerciseList", dtoList);
        return "preferences/preferences-create";
    }

    @PostMapping("/create")
    public String saveNewList(
            @RequestParam String listName,
            @RequestParam("exerciseId") List<Integer> exerciseIds,
            @RequestParam(value = "visible", required = false) List<Integer> visibleIds
    ) throws IOException {
        preferenceService.createNewList(listName, exerciseIds, visibleIds);
        return "redirect:/exercises";
    }



}
