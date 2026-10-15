package com.edvora.repository;

import com.edvora.model.Exam;
import com.edvora.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByUserOrderByExamDateTimeAsc(User u);
}
