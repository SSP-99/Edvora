package com.edvora.service;

import com.edvora.dto.CoveragePredictionDto;
import com.edvora.dto.DashboardDto;
import com.edvora.dto.ExamCountdownDto;
import com.edvora.dto.SubjectProgressDto;
import com.edvora.model.StudySession;
import com.edvora.model.Subject;
import com.edvora.model.Topic;
import com.edvora.model.User;
import com.edvora.model.enums.SessionStatus;
import com.edvora.model.enums.TopicStatus;
import com.edvora.repository.StudySessionRepository;
import com.edvora.repository.SubjectRepository;
import com.edvora.repository.TopicRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final StudySessionRepository studySessionRepository;
    private final ExamService examService;
    private final TimetableService timetableService;
    private final PredictorService predictorService;

    public AnalyticsServiceImpl(SubjectRepository subjectRepository, TopicRepository topicRepository,
                                StudySessionRepository studySessionRepository, ExamService examService,
                                TimetableService timetableService, PredictorService predictorService) {
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.studySessionRepository = studySessionRepository;
        this.examService = examService;
        this.timetableService = timetableService;
        this.predictorService = predictorService;
    }

    @Override
    public List<SubjectProgressDto> getSubjectProgress(User u) {
        List<Subject> subjects = subjectRepository.findByUser(u);
        List<SubjectProgressDto> list = new ArrayList<>();
        for (Subject s : subjects) {
            List<Topic> topics = topicRepository.findBySubject(s);
            int total = topics.size();
            int completed = 0;
            for (Topic t : topics) {
                if (t.getStatus() == TopicStatus.COMPLETED) {
                    completed++;
                }
            }
            int percent = total == 0 ? 0 : (completed * 100) / total;
            list.add(new SubjectProgressDto(s.getId(), s.getName(), total, completed, percent));
        }
        return list;
    }

    @Override
    public Map<LocalDate, Integer> getWeeklyMinutes(User u) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(6);
        List<StudySession> sessions = studySessionRepository.findByUserAndSessionDateBetweenOrderBySessionDateAsc(u, start, today);
        Map<LocalDate, Integer> map = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            map.put(today.minusDays(i), 0);
        }
        for (StudySession s : sessions) {
            if (s.getStatus() == SessionStatus.DONE) {
                map.put(s.getSessionDate(), map.getOrDefault(s.getSessionDate(), 0) + s.getActualMinutes());
            }
        }
        return map;
    }

    @Override
    public int getStreak(User u) {
        LocalDate today = LocalDate.now();
        int streak = 0;
        LocalDate current = today;
        boolean hasToday = false;
        List<StudySession> todaySessions = studySessionRepository.findByUserAndSessionDate(u, today);
        for (StudySession s : todaySessions) {
            if (s.getStatus() == SessionStatus.DONE) {
                hasToday = true;
                break;
            }
        }
        if (!hasToday) {
            current = today.minusDays(1);
        }
        while (true) {
            List<StudySession> daySessions = studySessionRepository.findByUserAndSessionDate(u, current);
            boolean doneFound = false;
            for (StudySession s : daySessions) {
                if (s.getStatus() == SessionStatus.DONE) {
                    doneFound = true;
                    break;
                }
            }
            if (doneFound) {
                streak++;
                current = current.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }

    @Override
    public int getProductivityScore(User u) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(6);
        List<StudySession> sessions = studySessionRepository.findByUserAndSessionDateBetweenOrderBySessionDateAsc(u, start, today);
        int planned = 0;
        int done = 0;
        for (StudySession s : sessions) {
            planned += s.getPlannedMinutes();
            if (s.getStatus() == SessionStatus.DONE) {
                done += s.getActualMinutes();
            }
        }
        double part1 = planned == 0 ? 0.0 : (double) done / planned;
        int streak = getStreak(u);
        double part2 = (double) Math.min(streak, 7) / 7.0;
        double score = (0.6 * part1 + 0.4 * part2) * 100.0;
        return (int) Math.round(score);
    }

    @Override
    public DashboardDto getDashboard(User u) {
        List<ExamCountdownDto> countdowns = examService.getCountdowns(u);
        List<StudySession> todaySessions = timetableService.getForDate(u, LocalDate.now());
        int streak = getStreak(u);
        int score = getProductivityScore(u);
        List<CoveragePredictionDto> predictions = predictorService.predict(u);
        return new DashboardDto(countdowns, todaySessions, streak, score, predictions);
    }
}
