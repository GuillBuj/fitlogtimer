package com.fitlogtimer.controller;


import com.fitlogtimer.dto.chart.ChartPeriodDataPointDTO;
import com.fitlogtimer.service.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/charts")
@RequiredArgsConstructor
public class ChartController {

    private final ChartService chartService;

    @GetMapping("/monthly-main-exercises")
    public String showMonthlyChart(Model model) {

        List<ChartPeriodDataPointDTO> data = chartService.getMainLiftsChartDataMonthly();

        model.addAttribute("chartData", data);
        return "monthly-chart";
    }
}
