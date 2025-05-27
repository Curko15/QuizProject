package ppks.projekt.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ppks.projekt.backend.dto.userdto.SignUpDto;
import ppks.projekt.backend.dto.userdto.UserDto;
import ppks.projekt.backend.entitiy.User;
import ppks.projekt.backend.enums.Role;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", expression = "java(determineRole(signUpDto))")
    User signUpToUser(SignUpDto signUpDto);

    default Role determineRole(SignUpDto dto) {
        return "TEACHER".equalsIgnoreCase(dto.role()) ? Role.TEACHER : Role.STUDENT;
    }
}
