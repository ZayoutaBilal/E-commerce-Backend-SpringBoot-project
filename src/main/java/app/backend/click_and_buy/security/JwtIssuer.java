package app.backend.click_and_buy.security;

import com.auth0.jwt.JWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class JwtIssuer {
    private final JwtProperties jwtProperties;
    public String issue(long userId, String email, String username, Set<GrantedAuthority> roles){
        List<String> rolesColl = roles.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(Instant.now().plus(Duration.of(jwtProperties.getAmount(), jwtProperties.getChronoUnit())))
                .withClaim("email",email)
                .withClaim("roles",rolesColl)
                .withClaim("username",username)
                .sign(jwtProperties.getAlgorithm());
    }
}
