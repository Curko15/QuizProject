package ppks.projekt.backend.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import ppks.projekt.backend.dto.userdto.UserDto;
import ppks.projekt.backend.entitiy.User;
import ppks.projekt.backend.exception.AppException;
import ppks.projekt.backend.mappers.UserMapper;
import ppks.projekt.backend.repository.UserRepository;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class UserAuthProvider {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Value("$security.jwt.token.secret-key:secret-key")
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(UserDto userDto) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 3_600_000);
        return JWT.create()
                .withSubject(userDto.getLogin())
                .withIssuedAt(Instant.now())
                .withExpiresAt(validity)
                .withClaim("firstName", userDto.getFirstName())
                .withClaim("lastName", userDto.getLastName())
                .withClaim("Role", userDto.getRole())
                .withClaim("login", userDto.getLogin())
                .sign(Algorithm.HMAC256(secretKey));
    }

    public Authentication validateToken(String token){

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);
        User user = userRepository.findByLogin(jwt.getSubject())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());
        return new UsernamePasswordAuthenticationToken(user, null, List.of(authority));
    }

    public Authentication validateTokenStrongly(String token){
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);

       User user = userRepository.findByLogin(jwt.getSubject())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());
        return new UsernamePasswordAuthenticationToken(user, null, List.of(authority));
    }
}
