package com.edvora.service;

import com.edvora.model.Topic;
import com.edvora.model.Exam;
import com.edvora.model.StudySession;
import com.edvora.model.enums.SessionType;
import com.edvora.model.enums.SessionStatus;
import com.edvora.repository.StudySessionRepository;
import com.edvora.repository.ExamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class RevisionService {

    private final StudySessionRepository studySessionRepository;
    private final ExamRepository examRepository;

    public RevisionService(StudySessionRepository studySessionRepository, ExamRepository examRepository) {
        this.studySessionRepository = studySessionRepository;
        this.examRepository = examRepository;
    }

    public void scheduleRevisions(Topic t) {
        if (t.getCompletedAt() == null) return;
        
        LocalDate baseDate = t.getCompletedAt().toLocalDate();
        List<Exam> exams = examRepository.findByUserOrderByExamDateTimeAsc(t.getSubject().getUser());
        
        Exam nearestExam = exams.stream()
                .filter(e -> e.getSubject().getId().equals(t.getSubject().getId()) && !e.getExamDateTime().toLocalDate().isBefore(baseDate))
                .findFirst().orElse(null);

        List<Integer> intervals = Arrays.asList(1, 3, 7, 14);
        
        for (int interval : intervals) {
            LocalDate revisionDate = baseDate.plusDays(interval);
            
            if (nearestExam != null && revisionDate.isAfter(nearestExam.getExamDateTime().toLocalDate())) {
                continue;
            }

            StudySession session = new StudySession();
            session.setUser(t.getSubject().getUser());
            session.setTopic(t);
            session.setSessionDate(revisionDate);
            session.setPlannedMinutes(30);
            session.setActualMinutes(0);
            session.setType(SessionType.REVISION);
            session.setStatus(SessionStatus.PLANNED);
            studySessionRepository.save(session);
        }
    }
}
