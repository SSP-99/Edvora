package com.edvora.service;

import com.edvora.exception.ResourceNotFoundException;
import com.edvora.model.Subject;
import com.edvora.model.Topic;
import com.edvora.model.enums.TopicStatus;
import com.edvora.repository.SubjectRepository;
import com.edvora.repository.TopicRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;
    private final RevisionService revisionService;

    public TopicService(TopicRepository topicRepository, SubjectRepository subjectRepository, RevisionService revisionService) {
        this.topicRepository = topicRepository;
        this.subjectRepository = subjectRepository;
        this.revisionService = revisionService;
    }

    public Topic add(Long subjectId, String title, double estimatedHours) {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setEstimatedHours(estimatedHours);
        topic.setStatus(TopicStatus.PENDING);
        topic.setSubject(subject);
        return topicRepository.save(topic);
    }

    public List<Topic> listBySubject(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        return topicRepository.findBySubject(subject);
    }

    public void markCompleted(Long topicId) {
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
        topic.setStatus(TopicStatus.COMPLETED);
        topic.setCompletedAt(LocalDateTime.now());
        topicRepository.save(topic);
        revisionService.scheduleRevisions(topic);
    }

    public void delete(Long id) {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
        topicRepository.delete(topic);
    }
}
