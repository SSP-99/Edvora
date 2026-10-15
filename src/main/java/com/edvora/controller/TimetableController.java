package com.edvora.controller;

import com.edvora.model.StudySession;
import com.edvora.model.User;
import com.edvora.service.RescheduleService;
import com.edvora.service.TimetableService;
import com.edvora.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class TimetableController {

    private final TimetableService timetableService;
    private final RescheduleService rescheduleService;
    private final UserService userService;

    public TimetableController(TimetableService timetableService, RescheduleService rescheduleService, UserService userService) {
        this.timetableService = timetableService;
        this.rescheduleService = rescheduleService;
        this.userService = userService;
    }

    @GetMapping("/timetable")
    public String view(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       Model model, Principal principal) {
        User u = userService.findByEmail(principal.getName());
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<StudySession> sessions = timetableService.getForDate(u, targetDate);
        model.addAttribute("sessions", sessions);
        model.addAttribute("date", targetDate);
        model.addAttribute("from", LocalDate.now());
        model.addAttribute("to", LocalDate.now().plusDays(13));
        return "timetable";
    }

    @PostMapping("/timetable/generate")
    public String generate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                           Principal principal) {
        User u = userService.findByEmail(principal.getName());
        timetableService.generateTimetable(u, from, to);
        return "redirect:/timetable?date=" + from;
    }

    @PostMapping("/timetable/done/{id}")
    public String markDone(@PathVariable Long id, @RequestParam int actualMinutes, @RequestParam String date) {
        timetableService.markDone(id, actualMinutes);
        return "redirect:/timetable?date=" + date;
    }

    @PostMapping("/timetable/reschedule")
    public String reschedule(Principal principal) {
        User u = userService.findByEmail(principal.getName());
        rescheduleService.rescheduleMissed(u);
        return "redirect:/timetable";
    }

    @GetMapping("/timetable/export")
    public ResponseEntity<byte[]> export(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                         Principal principal) {
        User u = userService.findByEmail(principal.getName());
        String csv = timetableService.exportCsv(u, from, to);
        byte[] data = csv.getBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=edvora-timetable.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(data.length)
                .body(data);
    }
}
