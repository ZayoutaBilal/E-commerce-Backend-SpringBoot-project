package app.backend.click_and_buy.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class UserDetailsDTO implements UserDetails {

    private long userId;
    private String username;
    private String password;
    private String email;
    private Set<GrantedAuthority> authorities;
    private boolean isEnabled;
    private String token;
    private boolean isActive;



    @JsonIgnore
    public String getPassword() {
        return this.password;
    }

    @JsonGetter("authorities")
    public List<String> getAuthoritiesList() {
        return this.authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return this.isActive;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
