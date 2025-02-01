package app.backend.click_and_buy.controllers;

import app.backend.click_and_buy.entities.*;
import app.backend.click_and_buy.enums.Numbers;
import app.backend.click_and_buy.massages.*;
import app.backend.click_and_buy.dto.UserDetailsDTO;
import app.backend.click_and_buy.enums.Roles;
import app.backend.click_and_buy.massages.Error;
import app.backend.click_and_buy.repositories.CustomerRepository;
import app.backend.click_and_buy.request.*;
import app.backend.click_and_buy.responses.*;
import app.backend.click_and_buy.security.JwtIssuer;
import app.backend.click_and_buy.services.*;
import app.backend.click_and_buy.enums.Paths;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.eclipse.angus.mail.util.MailConnectException;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

import java.util.*;

import static app.backend.click_and_buy.enums.UserActionType.SEARCH;
import static app.backend.click_and_buy.enums.UserActionType.VIEW;
import static app.backend.click_and_buy.statics.Builder.buildProductResponseList;

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
    private final CategoryService categoryService;
    private final ProductImageService productImageService;
    private final ProductService productService;
    private final UserBehaviorService userBehaviorService;
    private final ProductVariationService productVariationService;



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


    @PostMapping("message/send-message")
    public ResponseEntity<?> sendMessage(@RequestBody @Valid UserMessage message) {
        System.out.println(message);
        if(messageService.save(message))
            return ResponseEntity.ok().body(messageSource.getMessage(Success.USER_MESSAGE,null, Locale.getDefault()));
        return ResponseEntity.internalServerError().body(messageSource.getMessage(Error.USER_MESSAGE,null, Locale.getDefault()));
    }

    @GetMapping("categories/get-categories")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok().body(categoryService.getCategoriesWithSubcategories());
    }

    @PostMapping("products/by-category")
    public ResponseEntity<?> showProductsByCategory(@RequestBody @Valid ProductsCategory productsCategory) {
        Page<Product> products =productService.findProductsByCategoryTree(productsCategory.getCategoryName(),productsCategory.getOrigin(),productsCategory.getPage(), Numbers.PARTIAL_PRODUCT_LIST_SIZE.getIntValue());
        try{
            userBehaviorService.save(
                    userService.findById(14, false),
                    SEARCH, productsCategory.getCategoryName()
            );
        }catch(Exception exception){
            System.out.println(exception.getMessage());
        }
        return new ResponseEntity<>(buildProductResponseList(products,productImageService), HttpStatus.OK);
    }

    @GetMapping("products/recent")
    public ResponseEntity<?> getRecentProducts() {
        Page<Product> products =productService.getRecentProducts(12);
        return new ResponseEntity<>(buildProductResponseList(products,productImageService), HttpStatus.OK);
    }

    @GetMapping("products/most-liked")
    public ResponseEntity<?> getMostLikedProducts() {
        Page<Product> products =productService.getMostLikedProducts(12);
        return new ResponseEntity<>(buildProductResponseList(products,productImageService), HttpStatus.OK);
    }


    @GetMapping("products/product-details")
    public ResponseEntity<?> showProductDetails(@RequestParam @NotNull(message = "Product id must have a value") @Min(message = "Product id must be greater than or equal to 1",value = 1) Long productId) {
        Product product = productService.findProductById(productId);
        if(product == null){
            return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.PRODUCT_NOT_EXISTS,null, Locale.getDefault()));
        }
        ArrayList<ProductVariation> productVariations=productVariationService.findAllProductVariationsByProduct(product);
        ArrayList<ProductImage> productImages=productImageService.getProductImagesByProduct(product);
        ArrayList<ColorSizeQuantityCombination> colorSizeQuantityCombinations =productService.generateColorSizeCombinations(productVariations);
        ArrayList<ProductReview> productReviews = (ArrayList<ProductReview>) productService.getTopReviewsForProduct(product);
        try{
            userBehaviorService.save(
                    userService.findById(14, false),
                    VIEW, product.getProductId()
            );
            System.out.println("it executed ");
        }catch(Exception e){
            System.out.println("user id = "+commonService.getUserIdFromToken());
            System.out.println(e.getMessage());
        }

        Rating rating=product.getRating();
        double productStars = rating == null ? 0.0 : rating.getAverageStars();
        int productTotalRatings = rating == null ? 0 : rating.getTotalRatings();
        return new ResponseEntity<>(ProductDetails.builder()
                .productId(product.getProductId())
                .productName(product.getName())
                .productDescription(product.getDescription())
                .productInformation(product.getInformation())
                .productPrice(product.getPrice())
                .productStars(productStars)
                .productTotalRatings(productTotalRatings)
                .productOldPrice(Objects.requireNonNullElse(product.getOldPrice(),0.0))
                .productImages(productImageService.getImagesFromProductImages(productImages))
                .colorSizeQuantityCombinations(colorSizeQuantityCombinations)
                .productCategory(categoryService.getCategoryHierarchyString(product))
                .productReviews(productReviews)
                .build(), HttpStatus.OK);

    }

    @GetMapping("products/product-colors-size-quantity-combination")
    public ResponseEntity<?> getProductColorSizeQuantityCombination(@RequestParam @NotNull(message = "Product id must have a value") @Min(message = "Product id must be greater than or equal to 1",value = 1) Long productId) {
        Product product = productService.findProductById(productId);
        if(product == null){
            return ResponseEntity.badRequest().body(messageSource.getMessage(Warning.PRODUCT_NOT_EXISTS,null, Locale.getDefault()));
        }
        ArrayList<ProductVariation> productVariations=productVariationService.findAllProductVariationsByProduct(product);
        return ResponseEntity.ok().body(productService.generateColorSizeCombinations(productVariations));
    }

    @GetMapping("products/new-user")
    public ResponseEntity<?> getRecommendationsForNewUser(
            @RequestParam(defaultValue = "0") int page) {

        Page<?> recommendations = userBehaviorService.getAllRecommendationsForNewUser(page);
        return ResponseEntity.ok(recommendations);
    }


}
