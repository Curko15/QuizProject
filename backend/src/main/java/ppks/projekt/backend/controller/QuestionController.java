package ppks.projekt.backend.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ppks.projekt.backend.dto.questiondto.QuestionDto;
import ppks.projekt.backend.entitiy.Question;
import ppks.projekt.backend.service.QuestionService;

import java.util.Arrays;
import java.util.List;

@RestController
@AllArgsConstructor
public class QuestionController {

    private QuestionService questionService;

    @PostMapping("/questions")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<QuestionDto> addQuestion(@RequestBody QuestionDto dto) {
        Question question = questionService.addQuestion(dto);

        List<String> optionsList = Arrays.stream(question.getOptions().split(","))
                .map(String::trim)  // uklanja prazne razmake
                .filter(opt -> !opt.isEmpty()) // osigurava da nema praznih vrijednosti
                .toList(); // koristi `.toList()` (Java 16+) ili `Collectors.toList()` (Java 8-15)

        return ResponseEntity.status(HttpStatus.CREATED).body(new QuestionDto(
                question.getQuiz().getQuizId(),
                question.getQuestionText(),
                optionsList,
                question.getCorrectAnswer()
        ));
    }

    @DeleteMapping("/questions/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Integer id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/questions/{quizId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<QuestionDto>> getQuestionsByQuiz(@PathVariable Integer quizId) {
        List<Question> questions = questionService.getQuestionsByQuizId(quizId);
        List<QuestionDto> questionDtos = questions.stream()
                .map(question -> new QuestionDto(
                        question.getQuiz().getQuizId(),
                        question.getQuestionText(),
                        List.of(question.getOptions().split(",")),
                        question.getCorrectAnswer()))
                .toList();
        return ResponseEntity.ok(questionDtos);
    }

}
