package app.backend.click_and_buy.security;

import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class JwtToPrincipalConverter {
    public UserPrincipal convert(DecodedJWT decodedJWT){
        return UserPrincipal.builder()
                .userId((Long.valueOf(decodedJWT.getSubject())))
                .email(decodedJWT.getClaim("email").asString())
                .username(decodedJWT.getClaim("username").asString())
                .authorities(extractAuthoritiesFromClaim(decodedJWT))
                .build();
    }
    private Collection<SimpleGrantedAuthority> extractAuthoritiesFromClaim(DecodedJWT decodedJWT){
        var claim=decodedJWT.getClaim("roles");
        if(claim.isNull() || claim.isMissing()) return List.of();
        return claim.asList(SimpleGrantedAuthority.class);
    }
}
