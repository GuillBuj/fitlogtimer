package com.fitlogtimer.controller;


import com.fitlogtimer.dto.chart.ChartPeriodDataPointDTO;
import com.fitlogtimer.service.ChartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/charts")
@RequiredArgsConstructor
@Slf4j
public class ChartController {

    private final ChartService chartService;

    @GetMapping("/monthly-main-exercises")
    public String showMonthlyMainExercisesChart(Model model) {

        List<ChartPeriodDataPointDTO> data = chartService.getMainLiftsChartDataMonthly();

        model.addAttribute("chartData", data);
        model.addAttribute("chartType", "main");
        model.addAttribute("chartPeriod", "MONTHLY");
        model.addAttribute("dataType", "MAX");
        return "period-chart";
    }

    @GetMapping("/weekly-main-exercises")
    public String showWeeklyMainExercisesChart(Model model) {

        List<ChartPeriodDataPointDTO> data = chartService.getMainLiftsChartDataWeekly();

        model.addAttribute("chartData", data);
        model.addAttribute("chartType", "main");
        model.addAttribute("chartPeriod", "WEEKLY");
        model.addAttribute("dataType", "MAX");
        return "period-chart";
    }

    @GetMapping("/monthly-bench-exercises")
    public String showMonthlyBenchExercisesChart(Model model) {

        List<ChartPeriodDataPointDTO> data = chartService.getBenchLiftsChartDataMonthly();

        model.addAttribute("chartData", data);
        model.addAttribute("chartType", "bench");
        model.addAttribute("chartPeriod", "MONTHLY");
        model.addAttribute("dataType", "MAX");
        return "period-chart";
    }

    @GetMapping("/weekly-bench-exercises")
    public String showWeeklyBenchExercisesChart(Model model) {

        List<ChartPeriodDataPointDTO> data = chartService.getBenchLiftsChartDataWeekly();

        model.addAttribute("chartData", data);
        model.addAttribute("chartType", "bench");
        model.addAttribute("chartPeriod", "WEEKLY");
        model.addAttribute("dataType", "MAX");
        return "period-chart";
    }

    @GetMapping("/monthly-main-exercises-1rmest")
    public String showMonthlyMainExercises1RMEstChart(Model model) {

        List<ChartPeriodDataPointDTO> data = chartService.getMainLiftsChartDataMonthly1RMEst();

        model.addAttribute("chartData", data);
        model.addAttribute("chartType", "main");
        model.addAttribute("chartPeriod", "MONTHLY");
        model.addAttribute("dataType", "EST");
        return "period-chart";
    }

    @GetMapping("/weekly-main-exercises-1rmest")
    public String showWeeklyMainExercises1RMEstChart(Model model) {

        List<ChartPeriodDataPointDTO> data = chartService.getMainLiftsChartDataWeekly1RMEst();

        model.addAttribute("chartData", data);
        model.addAttribute("chartType", "main");
        model.addAttribute("chartPeriod", "WEEKLY");
        model.addAttribute("dataType", "EST");
        return "period-chart";
    }
}
