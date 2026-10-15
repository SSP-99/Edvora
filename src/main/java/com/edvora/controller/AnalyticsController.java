package com.edvora.controller;

import com.edvora.dto.SubjectProgressDto;
import com.edvora.model.User;
import com.edvora.service.AnalyticsService;
import com.edvora.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UserService userService;

    public AnalyticsController(AnalyticsService analyticsService, UserService userService) {
        this.analyticsService = analyticsService;
        this.userService = userService;
    }

    @GetMapping("/analytics")
    public String view(Model model, Principal principal) {
        User u = userService.findByEmail(principal.getName());
        List<SubjectProgressDto> progress = analyticsService.getSubjectProgress(u);
        model.addAttribute("progress", progress);
        return "analytics";
    }

    @GetMapping("/api/analytics/weekly")
    @ResponseBody
    public Map<LocalDate, Integer> weekly(Principal principal) {
        User u = userService.findByEmail(principal.getName());
        return analyticsService.getWeeklyMinutes(u);
    }

    @GetMapping("/api/analytics/subjects")
    @ResponseBody
    public List<SubjectProgressDto> subjects(Principal principal) {
        User u = userService.findByEmail(principal.getName());
        return analyticsService.getSubjectProgress(u);
    }
}
