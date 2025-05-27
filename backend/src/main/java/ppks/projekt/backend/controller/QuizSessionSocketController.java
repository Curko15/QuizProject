package ppks.projekt.backend.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ppks.projekt.backend.entitiy.Question;
import ppks.projekt.backend.entitiy.QuizSession;
import ppks.projekt.backend.entitiy.StudentAnswer;
import ppks.projekt.backend.entitiy.User;
import ppks.projekt.backend.repository.QuestionRepository;
import ppks.projekt.backend.repository.QuizSessionRepository;
import ppks.projekt.backend.repository.StudentAnswerRepository;
import ppks.projekt.backend.repository.UserRepository;
import ppks.projekt.backend.service.QuizSessionService;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class QuizSessionSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final QuizSessionRepository quizSessionRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final QuizSessionService quizSessionService;

    @MessageMapping("/leave")
    public void handleLeave(Map<String, String> payload) {
        String accessCode = payload.get("accessCode");

        messagingTemplate.convertAndSend(
                "/topic/session/" + accessCode,
                payload
        );
    }

    @MessageMapping("/end")
    public void endQuiz(Map<String, String> payload) {
        String accessCode = payload.get("accessCode");
        QuizSession session = quizSessionRepository.findByAccessCode(accessCode).orElseThrow();

        // Optionally set session status to FINISHED and save
        session.setStatus(ppks.projekt.backend.enums.SessionStatus.FINISHED);
        quizSessionRepository.save(session);

        // Notify all students that the quiz has ended
        messagingTemplate.convertAndSend(
                "/topic/session/" + accessCode,
                Map.of("type", "ABORTED", "accessCode", accessCode)
        );
    }

    @MessageMapping("/start")
    public void startQuiz(Map<String, String> payload) {
        String accessCode = payload.get("accessCode");

        QuizSession session = quizSessionRepository.findWithStudentsByAccessCode(accessCode).orElseThrow();
        List<Question> questions = questionRepository.findByQuizOrderByQuestionIdAsc(session.getQuiz());

        if (!questions.isEmpty()) {
            Question first = questions.getFirst();
            quizSessionService.startQuestion(session, first, true);
        }
    }

    @MessageMapping("/next")
    public void sendNextQuestion(Map<String, String> payload) {
        String accessCode = payload.get("accessCode");

        QuizSession session = quizSessionRepository.findByAccessCode(accessCode).orElseThrow();
        quizSessionService.finishQuestion(accessCode);
        quizSessionService.sendLeaderboard(session);
    }

    @MessageMapping("/answer")
    public void handleAnswer(Map<String, String> payload) {
        String accessCode = payload.get("accessCode");
        String studentLogin = payload.get("student");
        String submittedAnswer = payload.get("answer");

        User user = userRepository.findByLogin(studentLogin).orElseThrow();
        QuizSession session = quizSessionRepository.findByAccessCode(accessCode).orElseThrow();
        Question question = questionRepository.findById(session.getCurrentQuestion().getQuestionId())
                .orElseThrow();

        boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(submittedAnswer);

        StudentAnswer answer = new StudentAnswer();
        answer.setSession(session);
        answer.setStudent(user);
        answer.setQuestion(question);
        answer.setSubmittedAnswer(submittedAnswer);
        answer.setIsCorrect(isCorrect);
        studentAnswerRepository.save(answer);

        messagingTemplate.convertAndSend(
                "/topic/session/" + accessCode,
                Map.of(
                        "type", "ANSWERED",
                        "student", studentLogin,
                        "questionId", question.getQuestionId()
                )
        );

    }
}

