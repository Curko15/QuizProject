package ppks.projekt.backend.entitiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Integer quizId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT") // Za duže opise
    private String description;

    @ManyToOne(fetch = FetchType.LAZY) // Veza N:1 prema User entitetu
    @JoinColumn(name = "creator_user_id", nullable = false) // Ime stupca u DB
    private User creator; // Polje koje drži referencu na kreatora

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    // Veze
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER) // EAGER jer često trebamo pitanja uz kviz
    private Set<Question> questions = new HashSet<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuizSession> sessions = new HashSet<>();
}
