package app.backend.click_and_buy.controllers;

import app.backend.click_and_buy.massages.*;
import app.backend.click_and_buy.dto.UserDetailsDTO;
import app.backend.click_and_buy.enums.Roles;
import app.backend.click_and_buy.massages.Error;
import app.backend.click_and_buy.repositories.CustomerRepository;
import app.backend.click_and_buy.request.*;
import app.backend.click_and_buy.entities.User;
import app.backend.click_and_buy.responses.SignUp;
import app.backend.click_and_buy.security.JwtIssuer;
import app.backend.click_and_buy.services.*;
import app.backend.click_and_buy.enums.Paths;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import org.eclipse.angus.mail.util.MailConnectException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Locale;
import java.util.Objects;

@RestController
@RequestMapping("/user/")
@Validated
@AllArgsConstructor
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final MailingService mailingService;
    private final MessageSource messageSource;
    private final JwtIssuer jwtIssuer;
    private final UserService userService;
    private final PasswordVerificationCodeService passwordVerificationCodeService;
    private final EmailConfirmationCodeService emailConfirmationCodeService;
    private final CustomerRepository customerRepository;
    private final CommonService commonService;
    private final MessageService messageService;



    @PostMapping("signup")
    public ResponseEntity<String> signup(@RequestBody @Valid UserSignup userSignup) throws MessagingException {
        ResponseEntity<?> response = commonService.signup(userSignup, Roles.CUSTOMER.getRoles(),false);
        SignUp signUp = new SignUp();
        signUp.setMessage(Objects.requireNonNull(response.getBody()).toString());
        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                String verificationCode = emailConfirmationCodeService.storeVerificationCode(userSignup.getUser().getEmail());
                mailingService.sendMail(verificationCode, userSignup.getUser().getEmail(), null,
                        Paths.TEMPLATE_CONFIRM_EMAIL.getResourcePath(), Subject.USER_CONFIRM_ADDRESS_EMAIL);
                signUp.setValue(verificationCode);
            } catch (MailConnectException ignored) {}
        }
        return ResponseEntity.status(response.getStatusCode()).body(Objects.requireNonNull(response.getBody()).toString());
    }


    //TEST
    @PostMapping("test")
    public ResponseEntity<String> test() {
        // Send welcome email
        try {
            mailingService.sendMail("bilal","bilal.zay02@gmail.com","message",Paths.TEMPLATE_EMAIL_SIGNUP.getResourcePath(),"Welcome to our platform!");
        } catch (MessagingException ignored) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }
        return ResponseEntity.ok().body("Email sent.");

    }



    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLogin loginRequest) {
        Authentication authenticationRequest =
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword());
        try {
            Authentication authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);
            UserDetails userDetails = (UserDetails) authenticationResponse.getPrincipal();
            UserDetailsDTO userDetailsDTO =(UserDetailsDTO) userDetails;

            String token = jwtIssuer.issue(userDetailsDTO.getUserId(), userDetailsDTO.getEmail(), userDetailsDTO.getUsername(), userDetailsDTO.getAuthorities());
            userDetailsDTO.setToken(token);
            return ResponseEntity.ok().body(userDetailsDTO);

        } catch (BadCredentialsException ignored) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource.getMessage(Warning.INVALID_LOGIN_BODY,null, Locale.getDefault()));
        }catch (DisabledException ignored) {
            return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.EMAIL_IS_NOT_CONFIRMED_YET,null, Locale.getDefault()));
        }
    }

    @GetMapping("send-confirmation-code")
    public ResponseEntity<?> sendConfirmationCode(@RequestParam @Valid String emailToConfirm){
        User user = userService.findByEmail(emailToConfirm,false);
        if (user != null) {
            try {
                String verificationCode = emailConfirmationCodeService.storeVerificationCode(emailToConfirm);
                String message= messageSource.getMessage(Body.EMAIL_CODE_CONFIRMATION,null,Locale.getDefault()).concat("/n").concat(verificationCode);
                mailingService.sendMail(user.getUsername(), user.getEmail(), message,
                        Paths.TEMPLATE_MESSAGE_TO_USER.getResourcePath(), Subject.USER_CONFIRM_ADDRESS_EMAIL);
                return ResponseEntity.ok().body(messageSource.getMessage(Success.VERIFICATION_CODE_SENT,null, Locale.getDefault()));
            }catch (MailConnectException ignored){
                return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.VERIFICATION_CODE_SENT_FAILED,null, Locale.getDefault()));
            }

        } else {
            return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.EMAIL_NOT_EXIST,null, Locale.getDefault()));
        }
    }

    @PostMapping("confirm-email")
    public ResponseEntity<?> confirmEmail(@RequestBody @Valid ConfirmEmail confirmEmail){
        User user = userService.findByEmail(confirmEmail.getEmail(),false);
        String storedCode = emailConfirmationCodeService.getVerificationCode(confirmEmail.getEmail());
        if(user!=null){
            if (storedCode != null && storedCode.equals(confirmEmail.getCode())) {
                userService.confirmEmail(user);
                emailConfirmationCodeService.removeVerificationCode(confirmEmail.getEmail());
                return ResponseEntity.ok().body(messageSource.getMessage(Success.EMAIL_CONFIRMED,null, Locale.getDefault()));
            } else {
                return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.INVALID_VERIFICATION_CODE,null, Locale.getDefault()));
            }
        }else{
            return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.EMAIL_NOT_EXIST,null, Locale.getDefault()));
        }
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam @Email String email) {
        User user = userService.findByEmail(email,false);
        if (user != null) {
            try {
                String verificationCode = passwordVerificationCodeService.storeVerificationCode(email);
                mailingService.sendMail(user.getUsername(), user.getEmail(), verificationCode,
                        Paths.TEMPLATE_FORGET_PASSWORD.getResourcePath(), Subject.USER_FORGET_PASSWORD);
                return ResponseEntity.ok().body(messageSource.getMessage(Success.VERIFICATION_CODE_SENT,null, Locale.getDefault()));
            }catch (MailConnectException ignored){
                return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.VERIFICATION_CODE_SENT_FAILED,null, Locale.getDefault()));
            }

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource.getMessage(Warning.EMAIL_NOT_EXIST,null, Locale.getDefault()));
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody UserForgetPassword userForgetPassword) {
        User user = userService.findByEmail(userForgetPassword.getEmail(),false);
        String storedCode = passwordVerificationCodeService.getVerificationCode(userForgetPassword.getEmail());
        System.out.println("storedCode : "+storedCode);
        System.out.println("code : "+userForgetPassword.getCode());
        if(user!=null){
            if (storedCode != null && storedCode.equals(userForgetPassword.getCode())) {
                commonService.updatePassword(user,null,userForgetPassword.getNewPassword(),true);
                passwordVerificationCodeService.removeVerificationCode(userForgetPassword.getEmail());
                return ResponseEntity.ok().body(messageSource.getMessage(Success.VERIFICATION_CODE_VALID,null, Locale.getDefault())
                                .concat(" ."+messageSource.getMessage(Success.PASSWORD_UPDATED,null, Locale.getDefault())));

            } else {
                return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.INVALID_VERIFICATION_CODE,null, Locale.getDefault()));
            }
        }else{
            return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.EMAIL_NOT_EXIST,null, Locale.getDefault()));
        }

    }

    //TEST
    @GetMapping("get-message")
    public ResponseEntity<?> getMessage() {
        return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.EMAIL_NOT_EXIST,null, Locale.getDefault()));
    }

    @PostMapping("message/send-message")
    public ResponseEntity<?> sendMessage(@RequestBody @Valid UserMessage message) {
        System.out.println(message);
        if(messageService.save(message))
            return ResponseEntity.ok().body(messageSource.getMessage(Success.USER_MESSAGE,null, Locale.getDefault()));
        return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.USER_MESSAGE,null, Locale.getDefault()));
    }

}
