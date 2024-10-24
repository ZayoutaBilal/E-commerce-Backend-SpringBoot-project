package app.backend.click_and_buy.controllers;
import app.backend.click_and_buy.massages.*;
import app.backend.click_and_buy.dto.CustomerDTO;
import app.backend.click_and_buy.entities.User;
import app.backend.click_and_buy.massages.Error;
import app.backend.click_and_buy.request.UserUpdatePassword;
import app.backend.click_and_buy.services.CommonService;
import app.backend.click_and_buy.services.CustomerService;
import app.backend.click_and_buy.services.MailingService;
import app.backend.click_and_buy.services.UserService;
import app.backend.click_and_buy.enums.Paths;
import io.jsonwebtoken.io.IOException;
import jakarta.mail.MessagingException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@RestController
@RequestMapping("/common/")
@Validated
public class CommonController {

    private final UserService userService;
    private final CustomerService customerService;
    private final CommonService commonService;
    private final MailingService mailingService;
    private final MessageSource messageSource;

    public CommonController(UserService userService, CustomerService customerService, CommonService commonService, MailingService mailingService, MessageSource messageSource) {

        this.userService = userService;
        this.customerService = customerService;
        this.commonService = commonService;
        this.mailingService = mailingService;
        this.messageSource=messageSource;
    }

    @PostMapping("update-password")
    public ResponseEntity<?> updatePassword(@RequestBody UserUpdatePassword userUpdatePassword) {
        User user=userService.findById(commonService.getUserIdFromToken(),false);
            int ret=commonService.updatePassword(user,userUpdatePassword.getOldPassword(),userUpdatePassword.getNewPassword(),false);
            if(ret==1){
                try {
                    mailingService.sendMail(user.getUsername(), user.getEmail(),
                            messageSource.getMessage(Body.USER_PASSWORD_HAS_BEEN_UPDATED,null,Locale.getDefault()),
                            Paths.TEMPLATE_MESSAGE_TO_USER.getResourcePath(), Subject.USER_PASSWORD_HAS_BEEN_UPDATED);
                }catch (MessagingException ignored) {
                }
            }
            return switch (ret) {
            case 0 -> ResponseEntity.badRequest().body(messageSource.getMessage(Warning.INVALID_OLD_PASSWORD,null, Locale.getDefault()));
            case 1 -> ResponseEntity.ok().body(messageSource.getMessage(Success.PASSWORD_UPDATED,null, Locale.getDefault()));
            default -> ResponseEntity.internalServerError().body(messageSource.getMessage(Error.UPDATE_PASSWORD_FAILED,null, Locale.getDefault()));
        };
    }

    @PostMapping("upload-picture")
    public ResponseEntity<?> uploadPicture(@RequestParam("file") MultipartFile file) {
        User user=userService.findById(commonService.getUserIdFromToken(),false);
        try {
            if(customerService.updateCustomerProfilePicture(user.getCustomer(),file)){
                return ResponseEntity.ok().body(messageSource.getMessage(Success.PICTURE_UPLOADED,null, Locale.getDefault()));
            }
            return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.PICTURE_UPLOAD_FAILED,null, Locale.getDefault()));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.PICTURE_UPLOAD_FAILED,null, Locale.getDefault()));
        }
    }

    @PostMapping("update-user-infos")
    public ResponseEntity<?> updateUserInfos(@RequestBody CustomerDTO customerInfos){
        User user=userService.findById(commonService.getUserIdFromToken(),false);
        boolean bool=customerService.updateCustomer(user.getCustomer(),customerInfos);
        if(bool){
            return ResponseEntity.ok().body(messageSource.getMessage(Success.CUSTOMER_INFOS_UPDATED,null, Locale.getDefault()));
        }else {
            return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.CUSTOMER_INFOS_UPDATED_FAILED,null, Locale.getDefault()));
        }
    }


    @DeleteMapping("delete-my-account")
    public ResponseEntity<?> deleteMyAccount(){
        User user=userService.findById(commonService.getUserIdFromToken(),false);
        boolean bool=userService.remove(user);
        if(bool){
            try {
                mailingService.sendMail(user.getUsername(), user.getEmail(),
                        messageSource.getMessage(Body.YOU_REMOVED_YOUR_ACCOUNT,null,Locale.getDefault()),
                        Paths.TEMPLATE_MESSAGE_TO_USER.getResourcePath(), Subject.USER_ACCOUNT_REMOVED);
            }catch (MessagingException ignored) {}
            return ResponseEntity.ok().body(messageSource.getMessage(Success.ACCOUNT_REMOVED,null, Locale.getDefault()));
        }else {
            return ResponseEntity.badRequest().body(messageSource.getMessage(Error.ACCOUNT_REMOVED_FAILED,null, Locale.getDefault()));
        }
        
    }

    @PostMapping("profile-picture")
    public ResponseEntity<byte[]> getProfilePicture() {
        byte[] image = userService.findById(commonService.getUserIdFromToken(),false).getCustomer().getPicture();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}
