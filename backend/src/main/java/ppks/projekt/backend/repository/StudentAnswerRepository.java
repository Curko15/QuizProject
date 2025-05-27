package ppks.projekt.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ppks.projekt.backend.entitiy.Question;
import ppks.projekt.backend.entitiy.QuizSession;
import ppks.projekt.backend.entitiy.StudentAnswer;

import java.util.List;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Integer> {
    List<StudentAnswer> findBySession(QuizSession session);

    @Query("SELECT a FROM StudentAnswer a JOIN FETCH a.student WHERE a.session = :session")
    List<StudentAnswer> findBySessionWithStudent(@Param("session") QuizSession session);

    // Dohvati sve odgovore na određeno pitanje u određenoj sesiji
    List<StudentAnswer> findBySessionAndQuestion(QuizSession session, Question question);
}
