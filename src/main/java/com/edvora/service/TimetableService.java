package com.edvora.service;

import com.edvora.model.User;
import com.edvora.model.Subject;
import com.edvora.model.Topic;
import com.edvora.model.Exam;
import com.edvora.model.StudySession;
import com.edvora.model.enums.Difficulty;
import com.edvora.model.enums.TopicStatus;
import com.edvora.model.enums.SessionType;
import com.edvora.model.enums.SessionStatus;
import com.edvora.repository.StudySessionRepository;
import com.edvora.repository.SubjectRepository;
import com.edvora.repository.TopicRepository;
import com.edvora.repository.ExamRepository;
import com.edvora.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TimetableService {

    private final StudySessionRepository studySessionRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final ExamRepository examRepository;

    public TimetableService(StudySessionRepository studySessionRepository,
                            SubjectRepository subjectRepository,
                            TopicRepository topicRepository,
                            ExamRepository examRepository) {
        this.studySessionRepository = studySessionRepository;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.examRepository = examRepository;
    }

    public void generateTimetable(User u, LocalDate from, LocalDate to) {
        studySessionRepository.deleteByUserAndStatusAndSessionDateGreaterThanEqual(u, SessionStatus.PLANNED, from);
        List<Subject> allSubjects = subjectRepository.findByUser(u);
        List<Exam> allExams = examRepository.findByUserOrderByExamDateTimeAsc(u);

        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            final LocalDate currentDay = date;
            List<Subject> validSubjects = new ArrayList<>();
            Map<Long, Exam> subjectNearestExam = new HashMap<>();

            for (Subject s : allSubjects) {
                List<Topic> nonCompleted = s.getTopics().stream()
                        .filter(t -> t.getStatus() != TopicStatus.COMPLETED)
                        .collect(Collectors.toList());
                if (nonCompleted.isEmpty()) continue;

                Exam nearest = allExams.stream()
                        .filter(e -> e.getSubject().getId().equals(s.getId()) && !e.getExamDateTime().toLocalDate().isBefore(currentDay))
                        .findFirst().orElse(null);

                if (nearest != null) {
                    validSubjects.add(s);
                    subjectNearestExam.put(s.getId(), nearest);
                }
            }

            if (validSubjects.isEmpty()) continue;

            Map<Long, Double> priorities = new HashMap<>();
            double totalPriority = 0.0;

            for (Subject s : validSubjects) {
                Exam exam = subjectNearestExam.get(s.getId());
                long daysLeft = ChronoUnit.DAYS.between(currentDay, exam.getExamDateTime().toLocalDate());
                if (daysLeft < 1) daysLeft = 1;

                double priority = s.getDifficulty().weight() * (1.0 / daysLeft);
                priorities.put(s.getId(), priority);
                totalPriority += priority;
            }

            if (totalPriority == 0.0) continue;

            int availableMinutes = (int) (u.getDailyStudyHours() * 60);
            int minutesLeftForDay = availableMinutes;

            for (int i = 0; i < validSubjects.size(); i++) {
                Subject s = validSubjects.get(i);
                double priority = priorities.get(s.getId());
                int allocatedMinutes;

                if (i == validSubjects.size() - 1) {
                    allocatedMinutes = minutesLeftForDay;
                } else {
                    allocatedMinutes = (int) Math.round((priority / totalPriority) * availableMinutes);
                    allocatedMinutes = (allocatedMinutes / 5) * 5;
                    if (allocatedMinutes > minutesLeftForDay) {
                        allocatedMinutes = minutesLeftForDay;
                    }
                }

                minutesLeftForDay -= allocatedMinutes;
                if (allocatedMinutes < 25) continue;

                List<Topic> nonCompletedTopics = s.getTopics().stream()
                        .filter(t -> t.getStatus() != TopicStatus.COMPLETED)
                        .sorted(Comparator.comparing(Topic::getId))
                        .collect(Collectors.toList());

                int remainingSubjectMinutes = allocatedMinutes;
                for (Topic t : nonCompletedTopics) {
                    if (remainingSubjectMinutes < 25) break;

                    int sessionMinutes = (remainingSubjectMinutes / 5) * 5;
                    remainingSubjectMinutes -= sessionMinutes;

                    StudySession session = new StudySession();
                    session.setUser(u);
                    session.setTopic(t);
                    session.setSessionDate(currentDay);
                    session.setPlannedMinutes(sessionMinutes);
                    session.setActualMinutes(0);
                    session.setType(SessionType.STUDY);
                    session.setStatus(SessionStatus.PLANNED);
                    studySessionRepository.save(session);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<StudySession> getForDate(User u, LocalDate d) {
        return studySessionRepository.findByUserAndSessionDate(u, d);
    }

    public void markDone(Long sessionId, int actualMinutes) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        session.setActualMinutes(actualMinutes);
        session.setStatus(SessionStatus.DONE);
        studySessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public String exportCsv(User u, LocalDate from, LocalDate to) {
        List<StudySession> sessions = studySessionRepository.findByUserAndSessionDateBetweenOrderBySessionDateAsc(u, from, to);
        StringBuilder sb = new StringBuilder();
        sb.append("Date,Subject,Topic,Type,PlannedMinutes,ActualMinutes,Status\n");

        for (StudySession s : sessions) {
            sb.append(s.getSessionDate()).append(",");
            sb.append(escapeCsv(s.getTopic().getSubject().getName())).append(",");
            sb.append(escapeCsv(s.getTopic().getTitle())).append(",");
            sb.append(s.getType()).append(",");
            sb.append(s.getPlannedMinutes()).append(",");
            sb.append(s.getActualMinutes()).append(",");
            sb.append(s.getStatus()).append("\n");
        }
        return sb.toString();
    }

    private String escapeCsv(String val) {
        if (val == null) return "";
        if (val.contains(",") || val.contains("\"") || val.contains("\n") || val.contains("\r")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }
}
