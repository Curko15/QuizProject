package ppks.projekt.backend.entitiy;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ppks.projekt.backend.enums.SessionStatus;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "quiz_sessions")
public class QuizSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Integer sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "access_code", nullable = false, unique = true, length = 10)
    private String accessCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private SessionStatus status = SessionStatus.WAITING;

    @ManyToOne(fetch = FetchType.LAZY) // Veza prema trenutnom pitanju
    @JoinColumn(name = "current_question_id") // nullable=true je default za ManyToOne
    private Question currentQuestion; // Može biti null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_user_id", nullable = false)
    private User teacher; // Učitelj koji je pokrenuo

    @CreationTimestamp
    @Column(name = "started_at", updatable = false)
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt; // Bit će null dok sesija traje

    // Veze
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentAnswer> studentAnswers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "session_students",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<User> students = new HashSet<>();
}
