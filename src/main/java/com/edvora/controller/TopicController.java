package com.edvora.controller;

import com.edvora.model.Subject;
import com.edvora.model.Topic;
import com.edvora.model.User;
import com.edvora.service.SubjectService;
import com.edvora.service.TopicService;
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
public class TopicController {

    private final TopicService topicService;
    private final SubjectService subjectService;
    private final UserService userService;

    public TopicController(TopicService topicService, SubjectService subjectService, UserService userService) {
        this.topicService = topicService;
        this.subjectService = subjectService;
        this.userService = userService;
    }

    @GetMapping("/subjects/{id}/topics")
    public String list(@PathVariable Long id, Model model, Principal principal) {
        User u = userService.findByEmail(principal.getName());
        List<Subject> subjects = subjectService.listFor(u);
        Subject subject = null;
        for (Subject s : subjects) {
            if (s.getId().equals(id)) {
                subject = s;
                break;
            }
        }
        List<Topic> topics = topicService.listBySubject(id);
        model.addAttribute("subject", subject);
        model.addAttribute("topics", topics);
        return "topics";
    }

    @PostMapping("/subjects/{id}/topics/add")
    public String add(@PathVariable Long id, @RequestParam String title, @RequestParam double estimatedHours) {
        topicService.add(id, title, estimatedHours);
        return "redirect:/subjects/" + id + "/topics";
    }

    @PostMapping("/topics/complete/{id}")
    public String complete(@PathVariable Long id, @RequestParam Long subjectId) {
        topicService.markCompleted(id);
        return "redirect:/subjects/" + subjectId + "/topics";
    }

    @PostMapping("/topics/delete/{id}")
    public String delete(@PathVariable Long id, @RequestParam Long subjectId) {
        topicService.delete(id);
        return "redirect:/subjects/" + subjectId + "/topics";
    }
}
