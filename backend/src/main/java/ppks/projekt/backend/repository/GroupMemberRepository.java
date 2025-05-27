package ppks.projekt.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ppks.projekt.backend.entitiy.GroupMember;
import ppks.projekt.backend.entitiy.GroupMemberId;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {

}
