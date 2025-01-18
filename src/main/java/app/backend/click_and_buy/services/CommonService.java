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
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Locale;


@Service
public class CommonService {

    private final Argon2PasswordEncoder argon2PasswordEncoder= Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    private final UserService userService;
    private final CustomerService customerService;
    private final CartService cartService;
    private CartRepository cartRepository;
    private UserRepository userRepository;
    private CustomerRepository customerRepository;
    private final MessageSource messageSource;

    public CommonService(UserService userService, CustomerService customerService, CartService cartService, MessageSource messageSource) {
        this.userService = userService;
        this.customerService = customerService;
        this.cartService = cartService;
        this.messageSource = messageSource;

    }

    public ResponseEntity<?> signup(UserSignup userSignup, List<String> roles ,boolean confirmEmailStatus) {
        System.out.println(userSignup);
        String username = userSignup.getUser().getUsername();
            String password = userSignup.getUser().getPassword();
            String email = userSignup.getUser().getEmail();

            String firstName=userSignup.getCustomer().getFirstName();
            String lastName=userSignup.getCustomer().getLastName();
            String gender=userSignup.getCustomer().getGender();
            String phone=userSignup.getCustomer().getPhone();
            LocalDate birthDay=userSignup.getCustomer().getBirthday();
            String address=userSignup.getCustomer().getAddress();
            String city=userSignup.getCustomer().getCity();

        if(!ObjectValidator.emailValidator(email)){
            return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.INVALID_EMAIL,null, Locale.getDefault()));
        }
        if(userService.existsByEmail(email)){
            return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.EMAIL_ALREADY_EXIST,null, Locale.getDefault()));
        }
        if(userService.existsByUsername(username)){
            return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.USERNAME_ALREADY_EXIST,null, Locale.getDefault()));
        }

        String hashedPassword=argon2PasswordEncoder.encode(password);
        Customer customer = new Customer(firstName, lastName, gender, phone, birthDay, address, city);

        User user = User.builder()
            .username(username)
            .password(hashedPassword)
            .email(email)
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
        if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUserId();
        } else {
            return 0;
        }
    }

    public Collection<? extends GrantedAuthority> getAuthoritiesFromToken() {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getAuthorities();
        } else {
            return null;
        }
    }

//    public ResponseEntity<?> forgotPassword(String email) {
//        User user = userService.findByEmail(email);
//        if(user != null){
//            user.setVerificationCode(VerificationCodeGenerator.generateVerificationCode());
//            userService.save(user);
//            return ResponseEntity.ok().body(messageResponseSuccess.getVERIFICATION_CODE_SENT_SUCCESSFUL_RESPONSE());
//        }else{
//            return ResponseEntity.badRequest().body(messageResponseWarning.getEMAIL_NOT_EXIST_RESPONSE());
//        }
//    }

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
                }else {
                    return 0;
                }
            }
        }catch (Exception e){
            return -1;
        }


    }
}
