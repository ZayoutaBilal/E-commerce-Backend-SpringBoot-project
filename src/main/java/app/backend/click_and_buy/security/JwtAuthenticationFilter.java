package app.backend.click_and_buy.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtToPrincipalConverter jwtToPrincipalConverter;
    private final JwtProperties jwtProperties;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> token = extractTokenFromRequest(request);
        if (token.isPresent()) {
            boolean authenticated = token
                    .map(this::decode)
                    .map(jwtToPrincipalConverter::convert)
                    .map(UserPrincipalAuthenticationToken::new)
                    .map(authentication -> {
                        if (authentication.isAuthenticated()) {
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            return true;
                        }
                        return false;
                    })
                    .orElse(false);

            if (!authenticated) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Token is not valid or has expired");
                response.getWriter().flush();
                return;
            }
        }
        System.out.println("Request authenticated, proceeding with filter chain");
        filterChain.doFilter(request, response);
    }






    private Optional<String> extractTokenFromRequest(HttpServletRequest httpServletRequest){
        // token="Bearer ########.######.#######"
        var token =httpServletRequest.getHeader("Authorization");
        if(StringUtils.hasText(token) && token.startsWith("Bearer ")){
            return Optional.of(token.substring(7));
        }
        return Optional.empty();
    }

    public DecodedJWT decode(String token){
        try{
            return JWT.require(jwtProperties.getAlgorithm())
                    .build()
                    .verify(token);
        }catch (JWTVerificationException je){
            return null;
        }

    }
}
