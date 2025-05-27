package ppks.projekt.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ppks.projekt.backend.dto.quizSessiondto.JoinRequestDto;
import ppks.projekt.backend.dto.quizSessiondto.QuizResultDto;
import ppks.projekt.backend.entitiy.Quiz;
import ppks.projekt.backend.entitiy.QuizSession;
import ppks.projekt.backend.entitiy.StudentAnswer;
import ppks.projekt.backend.entitiy.User;
import ppks.projekt.backend.repository.QuizRepository;
import ppks.projekt.backend.repository.QuizSessionRepository;
import ppks.projekt.backend.repository.StudentAnswerRepository;
import ppks.projekt.backend.repository.UserRepository;
import ppks.projekt.backend.service.QuizSessionService;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class QuizSessionController {
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuizSessionService quizSessionService;
    private final SimpMessagingTemplate messagingTemplate;
    private final QuizSessionRepository quizSessionRepository;
    private final StudentAnswerRepository studentAnswerRepository;

    @PostMapping("/start/{quizId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> startSession(@PathVariable Integer quizId, Authentication auth) {
        User teacher = (User) auth.getPrincipal();
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Kviz ne postoji"));

        QuizSession session = quizSessionService.createSession(quiz, teacher);

        return ResponseEntity.ok(session.getAccessCode());
    }

    @PostMapping("/join")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> joinSession(@RequestBody JoinRequestDto dto, Authentication auth) {
        User student = (User) auth.getPrincipal();
        QuizSession session = quizSessionService.joinSession(dto.accessCode(), student);

        // Emit WebSocket poruku na session kanal
        messagingTemplate.convertAndSend(
                "/topic/session/" + dto.accessCode(),
                Map.of("type", "JOIN", "student", student.getLogin(), "accessCode", dto.accessCode())
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/results/teacher/{login}")
    public List<QuizResultDto> getResultsForTeacher(@PathVariable String login) {
        User teacher = userRepository.findByLogin(login).orElseThrow();
        List<QuizSession> sessions = quizSessionRepository.findByTeacher(teacher);

        return sessions.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/results/student/{login}")
    public List<QuizResultDto> getResultsForStudent(@PathVariable String login) {
        User student = userRepository.findByLogin(login).orElseThrow();
        List<QuizSession> sessions = quizSessionRepository.findAllByStudentId(student.getId());

        return sessions.stream()
                .map(session -> {
                    List<QuizResultDto.ScoreEntry> entries = studentAnswerRepository.findBySessionWithStudent(session).stream()
                            .collect(Collectors.groupingBy(a -> a.getStudent().getLogin(),
                                    Collectors.summingInt(a -> a.getIsCorrect() ? 1 : 0)))
                            .entrySet().stream()
                            .map(e -> new QuizResultDto.ScoreEntry(e.getKey(), e.getValue()))
                            .sorted(Comparator.comparingInt(QuizResultDto.ScoreEntry::getScore).reversed())
                            .toList();

                    return new QuizResultDto(
                            session.getQuiz().getTitle(),
                            session.getAccessCode(),
                            session.getStartedAt().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                            entries
                    );
                })
                .toList();
    }


    private QuizResultDto mapToDto(QuizSession session) {
        List<StudentAnswer> answers = studentAnswerRepository.findBySessionWithStudent(session);

        Map<String, Integer> scores = answers.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getStudent().getLogin(),
                        Collectors.summingInt(a -> Boolean.TRUE.equals(a.getIsCorrect()) ? 1 : 0)
                ));

        List<QuizResultDto.ScoreEntry> leaderboard = scores.entrySet().stream()
                .map(e -> new QuizResultDto.ScoreEntry(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingInt(QuizResultDto.ScoreEntry::getScore).reversed())
                .toList();

        return new QuizResultDto(
                session.getQuiz().getTitle(),
                session.getAccessCode(),
                session.getStartedAt().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                leaderboard
        );
    }
}

