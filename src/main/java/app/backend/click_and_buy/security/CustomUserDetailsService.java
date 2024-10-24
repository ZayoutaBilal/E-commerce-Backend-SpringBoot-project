package app.backend.click_and_buy.security;

import app.backend.click_and_buy.dto.UserDetailsDTO;
import app.backend.click_and_buy.entities.User;
import app.backend.click_and_buy.services.UserService;
import app.backend.click_and_buy.statics.ConverterToAuthorities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }


    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userService.checkUser(login);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with login: " + login);
        }

        return UserDetailsDTO.builder()
                    .userId(user.getUserId())
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .email(user.getEmail())
                    .isEnabled(user.getEmailConfirmed())
                    .authorities(ConverterToAuthorities.convertToAuthorities(user.getRoles()))
                    .build();
    }
}