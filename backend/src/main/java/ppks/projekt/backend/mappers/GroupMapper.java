package ppks.projekt.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ppks.projekt.backend.dto.groupdto.GroupDto;
import ppks.projekt.backend.entitiy.Group;


@Mapper(componentModel = "spring")
public interface GroupMapper {
    @Mapping(target = "creator", expression = "java(getCreatorLogin(group))")
    GroupDto toGroupDto(Group group);

    @Mapping(target = "creator", ignore = true)
    Group toGroup(GroupDto groupDto);


    default String getCreatorLogin(Group group) {
        return group.getCreator().getLogin();
    }
}

