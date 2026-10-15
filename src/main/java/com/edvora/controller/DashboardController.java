package com.edvora.controller;

import com.edvora.dto.DashboardDto;
import com.edvora.model.User;
import com.edvora.service.AnalyticsService;
import com.edvora.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

@Controller
public class DashboardController {

    private final UserService userService;
    private final AnalyticsService analyticsService;

    public DashboardController(UserService userService, AnalyticsService analyticsService) {
        this.userService = userService;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User u = userService.findByEmail(principal.getName());
        DashboardDto dashboardDto = analyticsService.getDashboard(u);
        model.addAttribute("dashboard", dashboardDto);
        model.addAttribute("user", u);
        return "dashboard";
    }
}
