package ppks.projekt.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ppks.projekt.backend.entitiy.Group;
import ppks.projekt.backend.entitiy.User;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    List<Group> findByCreator(User user);
}
