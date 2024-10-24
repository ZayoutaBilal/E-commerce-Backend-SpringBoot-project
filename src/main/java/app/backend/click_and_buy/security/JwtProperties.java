package app.backend.click_and_buy.security;


import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.temporal.ChronoUnit;

@Getter
@Setter
@Configuration
@ConfigurationProperties("security.jwt")
public class JwtProperties {

    private String secretKey;

    private ChronoUnit chronoUnit;

    private int amount;

    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        // Initialize the algorithm after the secretKey is set
        algorithm = Algorithm.HMAC256(secretKey);
    }
}
