package com.edvora.repository;

import com.edvora.model.Subject;
import com.edvora.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByUser(User user);
}
