package com.edvora.service;

import com.edvora.exception.ResourceNotFoundException;
import com.edvora.model.Subject;
import com.edvora.model.User;
import com.edvora.model.enums.Difficulty;
import com.edvora.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public Subject create(User u, String name, Difficulty d) {
        Subject subject = new Subject();
        subject.setName(name);
        subject.setDifficulty(d);
        subject.setUser(u);
        return subjectRepository.save(subject);
    }

    public List<Subject> listFor(User u) {
        return subjectRepository.findByUser(u);
    }

    public void delete(Long id) {
        Subject s = subjectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        subjectRepository.delete(s);
    }
}
