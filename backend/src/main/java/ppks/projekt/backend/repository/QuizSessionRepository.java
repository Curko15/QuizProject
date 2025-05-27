package ppks.projekt.backend.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ppks.projekt.backend.entitiy.QuizSession;
import ppks.projekt.backend.entitiy.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSessionRepository extends JpaRepository<QuizSession, Integer> {

    Optional<QuizSession> findByAccessCode(String accessCode);

    @EntityGraph(attributePaths = {"students"})
    Optional<QuizSession> findWithStudentsByAccessCode(String accessCode);

    List<QuizSession> findByTeacher(User teacher);

    @Query("SELECT s FROM QuizSession s JOIN s.students u WHERE u.id = :studentId")
    List<QuizSession> findAllByStudentId(@Param("studentId") Long studentId);
}
