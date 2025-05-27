package ppks.projekt.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ppks.projekt.backend.entitiy.*;
import ppks.projekt.backend.enums.SessionStatus;
import ppks.projekt.backend.repository.QuestionRepository;
import ppks.projekt.backend.repository.QuizSessionRepository;
import ppks.projekt.backend.repository.StudentAnswerRepository;
import ppks.projekt.backend.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizSessionService {

    private final QuizSessionRepository quizSessionRepository;
    private final UserRepository userRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final QuestionRepository questionRepository;

    public QuizSession createSession(Quiz quiz, User teacher) {
        String accessCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        QuizSession session = QuizSession.builder()
                .quiz(quiz)
                .teacher(teacher)
                .accessCode(accessCode)
                .status(SessionStatus.WAITING)
                .build();

        return quizSessionRepository.save(session);
    }

    public QuizSession joinSession(String accessCode, User student) {
        QuizSession session = quizSessionRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new RuntimeException("Sesija s kodom ne postoji"));

        if (session.getStatus() != SessionStatus.WAITING) {
            throw new RuntimeException("Sesija je već pokrenuta");
        }

        session.getStudents().add(student);
        return quizSessionRepository.save(session);
    }

    public void startQuestion(QuizSession session, Question question, boolean initial) {
        session.setCurrentQuestion(question);
        quizSessionRepository.save(session);

        Map<String, Object> questionData = Map.of(
                "type", "QUESTION",
                "questionText", question.getQuestionText(),
                "options", Arrays.stream(question.getOptions().split(","))
                        .map(String::trim)
                        .toList(),
                "questionId", question.getQuestionId(),
                "accessCode", session.getAccessCode()
        );

        messagingTemplate.convertAndSend("/topic/session/" + session.getAccessCode(), questionData);

        if (initial) {
            sendInitialLeaderboard(session);
        }
    }

    @Transactional
    public void finishQuestion(String accessCode) {
        // Učitaj sesiju zajedno sa studentima
        QuizSession session = quizSessionRepository.findWithStudentsByAccessCode(accessCode)
                .orElseThrow(() -> new RuntimeException("Sesija nije pronađena"));

        Question question = session.getCurrentQuestion();

        // Odgovori koji su već stigli
        List<StudentAnswer> answers = studentAnswerRepository.findBySessionAndQuestion(session, question);
        Set<Long> answeredUserIds = answers.stream()
                .map(a -> a.getStudent().getId())
                .collect(Collectors.toSet());

        // Dodaj netočne odgovore onima koji nisu odgovorili
        for (User student : session.getStudents()) {
            if (!answeredUserIds.contains(student.getId())) {
                StudentAnswer missing = new StudentAnswer();
                missing.setSession(session);
                missing.setQuestion(question);
                missing.setStudent(student);
                missing.setIsCorrect(false);
                missing.setSubmittedAnswer("NO_ANSWER");
                studentAnswerRepository.save(missing);
            }
        }

        // Ljestvica + iduće pitanje
        sendLeaderboard(session);
        sendNextQuestionOrEnd(session);
    }

    public void sendLeaderboard(QuizSession session) {
        List<StudentAnswer> answers = studentAnswerRepository.findBySessionWithStudent(session);

        Map<String, Integer> scores = new HashMap<>();
        for (StudentAnswer ans : answers) {
            String login = ans.getStudent().getLogin();
            scores.put(login, scores.getOrDefault(login, 0) + (ans.getIsCorrect() ? 1 : 0));
        }

        List<Map<String, Object>> leaderboard = scores.entrySet().stream()
                .map(e -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("student", e.getKey());
                    entry.put("score", e.getValue());
                    return entry;
                })
                .toList();

        messagingTemplate.convertAndSend("/topic/session/" + session.getAccessCode(),
                Map.of("type", "LEADERBOARD", "entries", leaderboard));
    }

    public void sendInitialLeaderboard(QuizSession session) {
        List<Map<String, Object>> initialLeaderboard = session.getStudents().stream()
                .map(student -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("student", student.getLogin());
                    entry.put("score", 0);
                    return entry;
                })
                .toList();

        messagingTemplate.convertAndSend("/topic/session/" + session.getAccessCode(),
                Map.of("type", "LEADERBOARD", "entries", initialLeaderboard));
    }

    public void sendNextQuestionOrEnd(QuizSession session) {
        List<Question> questions = questionRepository.findByQuizOrderByQuestionIdAsc(session.getQuiz());
        int currentIndex = questions.indexOf(session.getCurrentQuestion());

        if (currentIndex + 1 < questions.size()) {
            Question next = questions.get(currentIndex + 1);
            startQuestion(session, next, false);
        } else {
            messagingTemplate.convertAndSend("/topic/session/" + session.getAccessCode(),
                    Map.of("type", "QUIZ_ENDED"));
        }
    }
}

