package ppks.projekt.backend.entitiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "group_members")
// @IdClass(GroupMemberId.class) // Alternativni način definiranja kompozitnog ključa
public class GroupMember {

    @EmbeddedId // Koristimo ugrađeni ID definiran u GroupMemberId klasi
    private GroupMemberId id = new GroupMemberId(); // Važno inicijalizirati

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupId") // Mapira 'groupId' polje iz EmbeddedId na ovu vezu
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentUserId") // Mapira 'studentUserId' polje iz EmbeddedId na ovu vezu
    @JoinColumn(name = "student_user_id")
    private User student;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private Instant joinedAt;
}
