package ppks.projekt.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ppks.projekt.backend.entitiy.Question;
import ppks.projekt.backend.entitiy.Quiz;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findByQuiz_QuizId(Integer quizId);

    @Modifying
    @Query("DELETE FROM Question q WHERE q.quiz.quizId = :quizId")
    void deleteQuestionsByQuizId(@Param("quizId") Integer quizId);

    List<Question> findByQuizOrderByQuestionIdAsc(Quiz quiz);
}
