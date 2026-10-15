package com.edvora.config;

import com.edvora.model.User;
import com.edvora.model.Subject;
import com.edvora.model.Topic;
import com.edvora.model.Exam;
import com.edvora.model.enums.Difficulty;
import com.edvora.model.enums.TopicStatus;
import com.edvora.repository.UserRepository;
import com.edvora.repository.SubjectRepository;
import com.edvora.repository.TopicRepository;
import com.edvora.repository.ExamRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final ExamRepository examRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, SubjectRepository subjectRepository, TopicRepository topicRepository, ExamRepository examRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.examRepository = examRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("demo@edvora.com").isPresent()) {
            return;
        }
        User user = new User();
        user.setFullName("Demo User");
        user.setEmail("demo@edvora.com");
        user.setPassword(passwordEncoder.encode("demo123"));
        user.setDailyStudyHours(4.0);
        user = userRepository.save(user);

        Subject math = new Subject();
        math.setName("Mathematics");
        math.setDifficulty(Difficulty.HARD);
        math.setUser(user);
        math.setTopics(new ArrayList<>());
        math = subjectRepository.save(math);

        Subject phys = new Subject();
        phys.setName("Physics");
        phys.setDifficulty(Difficulty.MEDIUM);
        phys.setUser(user);
        phys.setTopics(new ArrayList<>());
        phys = subjectRepository.save(phys);

        Subject eng = new Subject();
        eng.setName("English");
        eng.setDifficulty(Difficulty.EASY);
        eng.setUser(user);
        eng.setTopics(new ArrayList<>());
        eng = subjectRepository.save(eng);

        String[][] topicsData = {
            {"Calculus", "Algebra", "Geometry", "Trigonometry"},
            {"Mechanics", "Thermodynamics", "Optics", "Electromagnetism"},
            {"Grammar", "Literature", "Writing", "Phonetics"}
        };

        Subject[] subjects = {math, phys, eng};
        for (int i = 0; i < subjects.length; i++) {
            for (int j = 0; j < 4; j++) {
                Topic topic = new Topic();
                topic.setTitle(topicsData[i][j]);
                topic.setEstimatedHours(3.0);
                topic.setStatus(TopicStatus.PENDING);
                topic.setSubject(subjects[i]);
                topicRepository.save(topic);
            }
        }

        Exam exam1 = new Exam();
        exam1.setName("Math Midterm");
        exam1.setExamDateTime(LocalDateTime.now().plusDays(10));
        exam1.setSubject(math);
        exam1.setUser(user);
        examRepository.save(exam1);

        Exam exam2 = new Exam();
        exam2.setName("Physics Finals");
        exam2.setExamDateTime(LocalDateTime.now().plusDays(20));
        exam2.setSubject(phys);
        exam2.setUser(user);
        examRepository.save(exam2);
    }
}
