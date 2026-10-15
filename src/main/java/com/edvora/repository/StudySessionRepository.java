package com.edvora.repository;

import com.edvora.model.StudySession;
import com.edvora.model.User;
import com.edvora.model.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {
    List<StudySession> findByUserAndSessionDate(User u, LocalDate d);
    List<StudySession> findByUserAndSessionDateBetweenOrderBySessionDateAsc(User u, LocalDate from, LocalDate to);
    List<StudySession> findByUserAndStatusAndSessionDateBefore(User u, SessionStatus s, LocalDate d);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM StudySession s WHERE s.user = ?1 AND s.status = ?2 AND s.sessionDate >= ?3")
    void deleteByUserAndStatusAndSessionDateGreaterThanEqual(User u, SessionStatus s, LocalDate d);
}
