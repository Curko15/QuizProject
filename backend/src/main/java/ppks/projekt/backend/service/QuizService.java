package ppks.projekt.backend.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ppks.projekt.backend.dto.quiz.CreateQuizDto;
import ppks.projekt.backend.entitiy.Quiz;
import ppks.projekt.backend.entitiy.User;
import ppks.projekt.backend.repository.QuizRepository;
import ppks.projekt.backend.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class QuizService {

    private QuizRepository quizRepository;
    private UserRepository userRepository;

    public Quiz createQuiz(CreateQuizDto dto, String name) {
        User creator = userRepository.findByLogin(name).orElseThrow(()->new EntityNotFoundException("User not found"));
        Quiz quiz = new Quiz();
        quiz.setTitle(dto.getTitle());
        if (dto.getDescription() != null) quiz.setDescription(dto.getDescription());
        quiz.setCreator(creator);
        return quizRepository.save(quiz);
    }

    public List<Quiz> getQuizzesByCreator(String login) {
        return quizRepository.findByCreator_Login(login);
    }

    public void deleteQuiz(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Kviz nije pronađen"));

        quizRepository.delete(quiz);
    }
}
