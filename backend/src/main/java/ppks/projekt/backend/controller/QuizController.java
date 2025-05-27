package ppks.projekt.backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ppks.projekt.backend.dto.quiz.CreateQuizDto;
import ppks.projekt.backend.dto.quiz.QuizDto;
import ppks.projekt.backend.entitiy.Quiz;
import ppks.projekt.backend.entitiy.User;
import ppks.projekt.backend.mappers.QuizMapper;
import ppks.projekt.backend.service.QuizService;

import java.util.List;

@RestController
@AllArgsConstructor
public class QuizController {

    private QuizService quizService;
    private QuizMapper quizMapper;

    @PostMapping("/quiz")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<QuizDto> createQuiz(@RequestBody CreateQuizDto dto, Authentication auth) {
        User user = (User) auth.getPrincipal();
        Quiz created = quizService.createQuiz(dto, user.getLogin());
        return ResponseEntity.status(HttpStatus.CREATED).body(quizMapper.toQuizDto(created));
    }

    @GetMapping("/quizzes")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<QuizDto>> getQuizzes(Authentication auth) {
        User user = (User) auth.getPrincipal();
        List<Quiz> quizzes = quizService.getQuizzesByCreator(user.getLogin());
        List<QuizDto> quizDtos = quizzes.stream()
                .map(quizMapper::toQuizDto)
                .toList();
        return ResponseEntity.ok(quizDtos);
    }

    @DeleteMapping("/quizzes/{quizId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Integer quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }



}
