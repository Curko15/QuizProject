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
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    // Jednostavna verzija za JSONB kao String
    @Column(columnDefinition = "TEXT") // Ili JSONB ako DB podržava i driver je podešen
    private String options; // Pohranjuje JSON string npr. "[\"Opcija A\", \"Opcija B\"]"


    @Column(name = "correct_answer", nullable = false, columnDefinition = "TEXT")
    private String correctAnswer; // Pohranjuje tekst točnog odgovora

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    // Veze
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentAnswer> studentAnswers = new HashSet<>();


}