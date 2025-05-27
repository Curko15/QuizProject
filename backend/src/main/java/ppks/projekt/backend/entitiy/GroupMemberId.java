package ppks.projekt.backend.entitiy;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable // Označava da se ova klasa može ugraditi u drugu entitetsku klasu
public class GroupMemberId implements Serializable {
    // Mora implementirati Serializable za kompozitne ključeve

    private Integer groupId;
    private Integer studentUserId;

    // Potrebno je implementirati equals() i hashCode() za kompozitne ključeve
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupMemberId that = (GroupMemberId) o;
        return Objects.equals(groupId, that.groupId) &&
                Objects.equals(studentUserId, that.studentUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, studentUserId);
    }
}
