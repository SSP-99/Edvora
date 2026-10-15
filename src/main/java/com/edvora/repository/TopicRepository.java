package com.edvora.repository;

import com.edvora.model.Subject;
import com.edvora.model.Topic;
import com.edvora.model.enums.TopicStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findBySubject(Subject s);
    long countBySubjectAndStatus(Subject s, TopicStatus st);
}
