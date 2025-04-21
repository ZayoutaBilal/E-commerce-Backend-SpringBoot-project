package app.backend.click_and_buy.services;

import app.backend.click_and_buy.massages.Warning;
import app.backend.click_and_buy.entities.Cart;
import app.backend.click_and_buy.entities.Customer;
import app.backend.click_and_buy.entities.User;
import app.backend.click_and_buy.repositories.CartRepository;
import app.backend.click_and_buy.repositories.CustomerRepository;
import app.backend.click_and_buy.repositories.UserRepository;
import app.backend.click_and_buy.request.UserSignup;
import app.backend.click_and_buy.security.UserPrincipal;
import app.backend.click_and_buy.massages.Error;
import app.backend.click_and_buy.massages.Success;
import app.backend.click_and_buy.statics.ObjectValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Locale;


@Service
@RequiredArgsConstructor
public class CommonService {

    private final Argon2PasswordEncoder argon2PasswordEncoder= Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    private final UserService userService;
    private final CustomerService customerService;
    private final CartService cartService;
    private final ModelMapper modelMapper;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final MessageSource messageSource;

    @Transactional
    public ResponseEntity<?> signup(UserSignup userSignup, List<String> roles ,boolean confirmEmailStatus) {

        if(!ObjectValidator.emailValidator(userSignup.getUser().getEmail())){
            return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.INVALID_EMAIL,null, Locale.getDefault()));
        }
        if(userService.existsByEmail(userSignup.getUser().getEmail())){
            return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.EMAIL_ALREADY_EXIST,null, Locale.getDefault()));
        }
        if(userService.existsByUsername(userSignup.getUser().getUsername())){
            return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.USERNAME_ALREADY_EXIST,null, Locale.getDefault()));
        }

        String hashedPassword=argon2PasswordEncoder.encode(userSignup.getUser().getPassword());
        Customer customer = modelMapper.map(userSignup.getCustomer(),Customer.class);

        User user = User.builder()
            .username(userSignup.getUser().getUsername())
            .password(hashedPassword)
            .email(userSignup.getUser().getEmail())
            .emailConfirmed(confirmEmailStatus)
            .roles(roles)
            .deleted(false)
            .build();

        try {
            customer=customerService.save(customer);
            user.setCustomer(customer);
            userService.save(user);
            cartService.saveCart(Cart.builder().customer(customer).build());
            return ResponseEntity.ok().body(messageSource.getMessage(Success.USER_SIGNUP,null, Locale.getDefault()));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageSource.getMessage(Error.USER_SIGNUP_FAILED,null, Locale.getDefault()));
        }

    }

    public long getUserIdFromToken() {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal() instanceof UserPrincipal userPrincipal ? userPrincipal.getUserId() : 0;
    }

    public Collection<? extends GrantedAuthority> getAuthoritiesFromToken() {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal() instanceof UserPrincipal userPrincipal ? userPrincipal.getAuthorities() : null;
    }

    public int updatePassword(User user,String oldPassword, String newPassword ,boolean forForgetPassword) {
        try{
            String hashedPassword=argon2PasswordEncoder.encode(newPassword);
            if(forForgetPassword){
                user.setPassword(hashedPassword);
                userService.save(user);
                return 1;
            }else{
                if (argon2PasswordEncoder.matches(oldPassword, user.getPassword())){
                    user.setPassword(hashedPassword);
                    userService.save(user);
                    return 1;
                } return 0;
            }
        }catch (Exception ignored){
            return -1;
        }
    }
}
