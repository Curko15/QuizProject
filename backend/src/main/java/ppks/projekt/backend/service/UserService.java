package ppks.projekt.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ppks.projekt.backend.dto.userdto.CredentialsDto;
import ppks.projekt.backend.dto.userdto.SignUpDto;
import ppks.projekt.backend.dto.userdto.UserDto;
import ppks.projekt.backend.entitiy.User;
import ppks.projekt.backend.enums.Role;
import ppks.projekt.backend.exception.AppException;
import ppks.projekt.backend.mappers.UserMapper;
import ppks.projekt.backend.repository.UserRepository;

import java.nio.CharBuffer;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserDto login(CredentialsDto credentialsDto) {
        User user = userRepository.findByLogin(credentialsDto.login())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.password()), user.getPassword())){
            return userMapper.toUserDto(user);
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UserDto register(SignUpDto signUpDto) {
        Optional<User> user = userRepository.findByLogin(signUpDto.login());

        if (user.isPresent()) {
            throw new AppException("User already exists", HttpStatus.BAD_REQUEST);
        }


        User userMapped = userMapper.signUpToUser(signUpDto);
        userMapped.setRole(Objects.equals(signUpDto.role(), "STUDENT") ? Role.STUDENT : Role.TEACHER);
        userMapped.setPassword(passwordEncoder.encode(CharBuffer.wrap(signUpDto.password())));
        User savedUser =  userRepository.save(userMapped);
        return userMapper.toUserDto(savedUser);
    }
}
