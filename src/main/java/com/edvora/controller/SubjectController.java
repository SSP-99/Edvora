package com.edvora.controller;

import com.edvora.model.Subject;
import com.edvora.model.User;
import com.edvora.model.enums.Difficulty;
import com.edvora.service.SubjectService;
import com.edvora.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.List;

@Controller
public class SubjectController {

    private final SubjectService subjectService;
    private final UserService userService;

    public SubjectController(SubjectService subjectService, UserService userService) {
        this.subjectService = subjectService;
        this.userService = userService;
    }

    @GetMapping("/subjects")
    public String list(Model model, Principal principal) {
        User u = userService.findByEmail(principal.getName());
        List<Subject> subjects = subjectService.listFor(u);
        model.addAttribute("subjects", subjects);
        model.addAttribute("difficulties", Difficulty.values());
        return "subjects";
    }

    @PostMapping("/subjects/add")
    public String add(@RequestParam String name, @RequestParam Difficulty difficulty, Principal principal) {
        User u = userService.findByEmail(principal.getName());
        subjectService.create(u, name, difficulty);
        return "redirect:/subjects";
    }

    @PostMapping("/subjects/delete/{id}")
    public String delete(@PathVariable Long id) {
        subjectService.delete(id);
        return "redirect:/subjects";
    }
}
