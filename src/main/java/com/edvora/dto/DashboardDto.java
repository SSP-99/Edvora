package com.edvora.dto;

import com.edvora.model.StudySession;
import java.util.List;

public class DashboardDto {
    private List<ExamCountdownDto> countdowns;
    private List<StudySession> todaySessions;
    private int streak;
    private int productivityScore;
    private List<CoveragePredictionDto> predictions;

    public DashboardDto() {}

    public DashboardDto(List<ExamCountdownDto> countdowns, List<StudySession> todaySessions, int streak, int productivityScore, List<CoveragePredictionDto> predictions) {
        this.countdowns = countdowns;
        this.todaySessions = todaySessions;
        this.streak = streak;
        this.productivityScore = productivityScore;
        this.predictions = predictions;
    }

    public List<ExamCountdownDto> getCountdowns() { return countdowns; }
    public void setCountdowns(List<ExamCountdownDto> countdowns) { this.countdowns = countdowns; }
    public List<StudySession> getTodaySessions() { return todaySessions; }
    public void setTodaySessions(List<StudySession> todaySessions) { this.todaySessions = todaySessions; }
    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }
    public int getProductivityScore() { return productivityScore; }
    public void setProductivityScore(int productivityScore) { this.productivityScore = productivityScore; }
    public List<CoveragePredictionDto> getPredictions() { return predictions; }
    public void setPredictions(List<CoveragePredictionDto> predictions) { this.predictions = predictions; }
}
