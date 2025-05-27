package ppks.projekt.backend.entitiy;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ppks.projekt.backend.enums.Role;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Za SERIAL tip u PostgreSQL
    @Column(name = "user_id")
    private Long Id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String login;

//    @Column(nullable = false, unique = true, length = 100)
//    private String username;

////    @Column(nullable = false, unique = true)
////    private String email;
//
//    @Column(name = "password_hash", nullable = false)
//    private String passwordHash; // Ime polja može biti drugačije od stupca
//
    @Enumerated(EnumType.STRING) // Pohrani Enum kao String ('TEACHER' ili 'STUDENT')
    @Column(nullable = false, length = 10)
    private Role role;
//
//    @CreationTimestamp // Automatski postavlja vrijeme kreiranja
//    @Column(name = "created_at", updatable = false)
//    private Instant createdAt;

    // Veze (Relationships) - ovo pokazuje koje kvizove je korisnik kreirao, itd.
    // mappedBy pokazuje na ime polja u drugoj klasi koje definira vezu (@ManyToOne)

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Quiz> createdQuizzes = new HashSet<>();

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuizSession> startedSessions = new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentAnswer> answers = new HashSet<>();

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Group> createdGroups = new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GroupMember> groupMemberships = new HashSet<>();

}
