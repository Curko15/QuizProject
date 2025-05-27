package ppks.projekt.backend.config;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ppks.projekt.backend.entitiy.*;
import ppks.projekt.backend.enums.Role;
import ppks.projekt.backend.enums.SessionStatus;
import ppks.projekt.backend.repository.*;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInit {

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizSessionRepository quizSessionRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {

        User teacher = User.builder()
                .firstName("Ivana")
                .lastName("Horvat")
                .login("ivana.horvat")
                .password(passwordEncoder.encode("lozinka123"))
                .role(Role.TEACHER)
                .build();
        User student1 = User.builder()
                .firstName("Marko")
                .lastName("Maric")
                .login("marko.maric")
                .password(passwordEncoder.encode("lozinka123"))
                .role(Role.STUDENT)
                .build();
        User student2 = User.builder()
                .firstName("Ana")
                .lastName("Kovac")
                .login("ana.kovac")
                .password(passwordEncoder.encode("lozinka123"))
                .role(Role.STUDENT)
                .build();
        userRepository.saveAll(List.of(teacher, student1, student2));

        Quiz quiz = new Quiz();
        quiz.setTitle("Opće znanje");
        quiz.setDescription("Kviz iz općeg znanja za sve uzraste.");
        quiz.setCreator(teacher);
        quizRepository.save(quiz);


        Question q1 = new Question();
        q1.setQuiz(quiz);
        q1.setQuestionText("Koji je glavni grad Hrvatske?");
        q1.setOptions("Zagreb,Split,Rijeka,Osijek");
        q1.setCorrectAnswer("Zagreb");

        Question q2 = new Question();
        q2.setQuiz(quiz);
        q2.setQuestionText("Koja rijeka prolazi kroz grad Vukovar?");
        q2.setOptions("Sava,Drava,Dunav,Kupa");
        q2.setCorrectAnswer("Dunav");

        questionRepository.saveAll(List.of(q1, q2));
    }
}