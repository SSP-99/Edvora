package com.edvora.model;

import com.edvora.model.enums.SessionStatus;
import com.edvora.model.enums.SessionType;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "study_sessions")
public class StudySession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(nullable = false)
    private LocalDate sessionDate;

    @Column(nullable = false)
    private int plannedMinutes;

    @Column(nullable = false)
    private int actualMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.PLANNED;

    public StudySession() {}

    public StudySession(Long id, User user, Topic topic, LocalDate sessionDate, int plannedMinutes, int actualMinutes, SessionType type, SessionStatus status) {
        this.id = id;
        this.user = user;
        this.topic = topic;
        this.sessionDate = sessionDate;
        this.plannedMinutes = plannedMinutes;
        this.actualMinutes = actualMinutes;
        this.type = type;
        if (status != null) this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }
    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }
    public int getPlannedMinutes() { return plannedMinutes; }
    public void setPlannedMinutes(int plannedMinutes) { this.plannedMinutes = plannedMinutes; }
    public int getActualMinutes() { return actualMinutes; }
    public void setActualMinutes(int actualMinutes) { this.actualMinutes = actualMinutes; }
    public SessionType getType() { return type; }
    public void setType(SessionType type) { this.type = type; }
    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }
}
