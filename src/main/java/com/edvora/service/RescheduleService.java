package com.edvora.service;

import com.edvora.model.User;
import com.edvora.model.StudySession;
import com.edvora.model.enums.SessionStatus;
import com.edvora.repository.StudySessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class RescheduleService {

    private final StudySessionRepository studySessionRepository;

    public RescheduleService(StudySessionRepository studySessionRepository) {
        this.studySessionRepository = studySessionRepository;
    }

    public int rescheduleMissed(User u) {
        LocalDate today = LocalDate.now();
        List<StudySession> overduePlanned = studySessionRepository.findByUserAndStatusAndSessionDateBefore(u, SessionStatus.PLANNED, today);
        
        if (overduePlanned.isEmpty()) {
            return 0;
        }

        int count = overduePlanned.size();
        for (StudySession session : overduePlanned) {
            session.setStatus(SessionStatus.MISSED);
            studySessionRepository.save(session);
        }

        int dailyLimitMinutes = (int) (u.getDailyStudyHours() * 60);
        LocalDate targetDate = today;

        for (StudySession missedSession : overduePlanned) {
            while (true) {
                List<StudySession> daySessions = studySessionRepository.findByUserAndSessionDate(u, targetDate);
                int scheduledMinutes = daySessions.stream().mapToInt(StudySession::getPlannedMinutes).sum();
                
                if (scheduledMinutes + missedSession.getPlannedMinutes() <= dailyLimitMinutes) {
                    StudySession newSession = new StudySession();
                    newSession.setUser(u);
                    newSession.setTopic(missedSession.getTopic());
                    newSession.setSessionDate(targetDate);
                    newSession.setPlannedMinutes(missedSession.getPlannedMinutes());
                    newSession.setActualMinutes(0);
                    newSession.setType(missedSession.getType());
                    newSession.setStatus(SessionStatus.PLANNED);
                    studySessionRepository.save(newSession);
                    break;
                }
                targetDate = targetDate.plusDays(1);
            }
        }

        return count;
    }
}
