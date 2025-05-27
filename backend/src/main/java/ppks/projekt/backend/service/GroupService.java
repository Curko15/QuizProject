package ppks.projekt.backend.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ppks.projekt.backend.dto.groupdto.CreateGroupDto;
import ppks.projekt.backend.dto.groupdto.GroupDto;
import ppks.projekt.backend.entitiy.Group;
import ppks.projekt.backend.entitiy.User;
import ppks.projekt.backend.mappers.GroupMapper;
import ppks.projekt.backend.repository.GroupRepository;
import ppks.projekt.backend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMapper groupMapper;

    public List<GroupDto> getGroupsForUser(String name) {
        User user = userRepository.findByLogin(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Group> groups = groupRepository.findByCreator(user);

        return groups.stream()
                .map(groupMapper::toGroupDto)
                .collect(Collectors.toList());

    }

    public GroupDto createGroup(CreateGroupDto groupDto) {
        Group group = Group.builder()
                .groupName(groupDto.groupName())
                .creator(userRepository.findByLogin(groupDto.creatorLogin())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found")))
                .joinCode("BIT-CE-KOD")
                .build();

        return groupMapper.toGroupDto(groupRepository.save(group));
    }
}
