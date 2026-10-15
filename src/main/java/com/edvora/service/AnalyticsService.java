package com.edvora.service;

import com.edvora.dto.DashboardDto;
import com.edvora.dto.SubjectProgressDto;
import com.edvora.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AnalyticsService {
    List<SubjectProgressDto> getSubjectProgress(User u);
    Map<LocalDate, Integer> getWeeklyMinutes(User u);
    int getStreak(User u);
    int getProductivityScore(User u);
    DashboardDto getDashboard(User u);
}
