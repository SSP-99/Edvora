package com.edvora.service;

import com.edvora.dto.CoveragePredictionDto;
import com.edvora.model.Exam;
import com.edvora.model.Subject;
import com.edvora.model.Topic;
import com.edvora.model.User;
import com.edvora.model.enums.TopicStatus;
import com.edvora.repository.ExamRepository;
import com.edvora.repository.TopicRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class PredictorServiceImpl implements PredictorService {

    private final ExamRepository examRepository;
    private final TopicRepository topicRepository;

    public PredictorServiceImpl(ExamRepository examRepository, TopicRepository topicRepository) {
        this.examRepository = examRepository;
        this.topicRepository = topicRepository;
    }

    @Override
    public List<CoveragePredictionDto> predict(User u) {
        List<Exam> exams = examRepository.findByUserOrderByExamDateTimeAsc(u);
        LocalDate today = LocalDate.now();
        List<Exam> upcomingExams = new ArrayList<>();
        for (Exam e : exams) {
            if (!e.getExamDateTime().toLocalDate().isBefore(today)) {
                upcomingExams.add(e);
            }
        }
        int futureSubjectsCount = upcomingExams.size();
        List<CoveragePredictionDto> predictions = new ArrayList<>();
        for (Exam e : upcomingExams) {
            Subject s = e.getSubject();
            List<Topic> topics = topicRepository.findBySubject(s);
            double remainingHours = 0;
            for (Topic t : topics) {
                if (t.getStatus() != TopicStatus.COMPLETED) {
                    remainingHours += t.getEstimatedHours();
                }
            }
            long daysUntilExam = ChronoUnit.DAYS.between(today, e.getExamDateTime().toLocalDate());
            if (daysUntilExam < 0) daysUntilExam = 0;
            double availableHours = daysUntilExam * u.getDailyStudyHours() * (futureSubjectsCount == 0 ? 0.0 : (1.0 / futureSubjectsCount));
            String status = availableHours >= remainingHours ? "ON_TRACK" : "AT_RISK";
            double shortfallHours = Math.max(0, remainingHours - availableHours);
            predictions.add(new CoveragePredictionDto(s.getName(), remainingHours, availableHours, status, shortfallHours));
        }
        return predictions;
    }
}
