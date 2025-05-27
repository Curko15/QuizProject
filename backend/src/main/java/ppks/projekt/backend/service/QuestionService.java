package ppks.projekt.backend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ppks.projekt.backend.dto.questiondto.QuestionDto;
import ppks.projekt.backend.entitiy.Question;
import ppks.projekt.backend.entitiy.Quiz;
import ppks.projekt.backend.repository.QuestionRepository;
import ppks.projekt.backend.repository.QuizRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class QuestionService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public Question addQuestion(QuestionDto dto) {
        Quiz quiz = quizRepository.findById(dto.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Question question = new Question();
        question.setQuiz(quiz);
        question.setQuestionText(dto.getQuestionText());
        question.setOptions(String.join(",", dto.getOptions()));
        question.setCorrectAnswer(dto.getCorrectAnswer());

        return questionRepository.save(question);
    }

    public void deleteQuestion(Integer id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pitanje nije pronađeno"));
        questionRepository.delete(question);
    }

    public List<Question> getQuestionsByQuizId(Integer quizId) {
        return questionRepository.findByQuiz_QuizId(quizId);
    }

}
