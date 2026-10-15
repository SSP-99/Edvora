package com.edvora.service;

import com.edvora.dto.ExamCountdownDto;
import com.edvora.exception.ResourceNotFoundException;
import com.edvora.model.Exam;
import com.edvora.model.Subject;
import com.edvora.model.User;
import com.edvora.repository.ExamRepository;
import com.edvora.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;

    public ExamService(ExamRepository examRepository, SubjectRepository subjectRepository) {
        this.examRepository = examRepository;
        this.subjectRepository = subjectRepository;
    }

    public Exam add(User u, Long subjectId, String name, LocalDateTime dt) {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Exam exam = new Exam();
        exam.setName(name);
        exam.setExamDateTime(dt);
        exam.setSubject(subject);
        exam.setUser(u);
        return examRepository.save(exam);
    }

    public List<ExamCountdownDto> getCountdowns(User u) {
        List<Exam> exams = examRepository.findByUserOrderByExamDateTimeAsc(u);
        List<ExamCountdownDto> countdowns = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Exam e : exams) {
            long daysLeft = 0;
            long hoursLeft = 0;
            boolean expired = e.getExamDateTime().isBefore(now);
            if (!expired) {
                Duration duration = Duration.between(now, e.getExamDateTime());
                daysLeft = duration.toDays();
                hoursLeft = duration.toHours() % 24;
            }
            countdowns.add(new ExamCountdownDto(e.getId(), e.getName(), e.getSubject().getName(), daysLeft, hoursLeft, expired));
        }
        return countdowns;
    }

    public void delete(Long id) {
        Exam exam = examRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        examRepository.delete(exam);
    }
}
