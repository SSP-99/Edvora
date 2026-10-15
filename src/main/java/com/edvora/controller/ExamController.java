package com.edvora.controller;

import com.edvora.dto.ExamCountdownDto;
import com.edvora.model.Subject;
import com.edvora.model.User;
import com.edvora.service.ExamService;
import com.edvora.service.SubjectService;
import com.edvora.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ExamController {

    private final ExamService examService;
    private final SubjectService subjectService;
    private final UserService userService;

    public ExamController(ExamService examService, SubjectService subjectService, UserService userService) {
        this.examService = examService;
        this.subjectService = subjectService;
        this.userService = userService;
    }

    @GetMapping("/exams")
    public String list(Model model, Principal principal) {
        User u = userService.findByEmail(principal.getName());
        List<ExamCountdownDto> countdowns = examService.getCountdowns(u);
        List<Subject> subjects = subjectService.listFor(u);
        model.addAttribute("countdowns", countdowns);
        model.addAttribute("subjects", subjects);
        return "exams";
    }

    @PostMapping("/exams/add")
    public String add(@RequestParam Long subjectId, @RequestParam String name,
                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime examDateTime,
                      Principal principal) {
        User u = userService.findByEmail(principal.getName());
        examService.add(u, subjectId, name, examDateTime);
        return "redirect:/exams";
    }

    @PostMapping("/exams/delete/{id}")
    public String delete(@PathVariable Long id) {
        examService.delete(id);
        return "redirect:/exams";
    }
}
