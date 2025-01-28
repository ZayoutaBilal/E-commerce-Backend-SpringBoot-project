package app.backend.click_and_buy.massages;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//@Getter
//@Setter
//@Configuration
//@ConfigurationProperties("message.response.error")
public class Error {


    public static String USER_SIGNUP_FAILED = "message.response.error.user-signup-failed";
    public static String USER_LOGIN_FAILED = "message.response.error.user-login-failed";
    public static String USER_LOGOUT_FAILED = "message.response.error.user-logout-failed";
    public static String INVALID_REQUEST_BODY = "message.response.error.invalid-request-body";
    public static String VERIFY_EMAIL_FAILED = "message.response.error.verify-email-failed";
    public static String VERIFICATION_CODE_SENT_FAILED = "message.response.error.verification-code-sent-failed";
    public static String UPDATE_PASSWORD_FAILED = "message.response.error.update-password-failed";
    public static String PICTURE_UPLOAD_FAILED = "message.response.error.picture-upload-failed";
    public static String CUSTOMER_INFOS_UPDATED_FAILED = "message.response.error.customer-infos-updated-failed";
    public static String ACCOUNT_REMOVED_FAILED = "message.response.error.account-removed-failed";
    public static String FEEDBACK_FAILED = "message.response.error.adding-feedback-failed";
    public static String UPDATING_ITEM_IN_CART_FAILED = "message.response.error.updating-item-in-cart-failed";
    public static String ADDING_PRODUCT_TO_CART_FAILED = "message.response.error.adding-product-to-cart-failed";
    public static String USER_MESSAGE = "message.response.error.user-message";
    public static String ADD_CATEGORY = "message.response.error.add-category";
    public static String ADD_PRODUCT = "message.response.error.add-product";
    public static String DELETE_CATEGORY = "message.response.error.delete-category";
    public static String DELETE_PRODUCT = "message.response.error.delete-product";
    public static String UPDATE_CATEGORY = "message.response.error.update-category";
    public static String UPDATE_PRODUCT = "message.response.error.update-product";



}
