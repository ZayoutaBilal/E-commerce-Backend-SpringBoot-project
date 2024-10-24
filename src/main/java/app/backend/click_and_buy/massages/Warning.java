package app.backend.click_and_buy.massages;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
//@Getter
//@Setter
//@Configuration
//@ConfigurationProperties("message.response.warn")
public class Warning {

    public static String EMAIL_NOT_EXIST = "message.response.warn.email-not-exist";
    public static String EMAIL_ALREADY_EXIST = "message.response.warn.email-already-exist";
    public static String USERNAME_ALREADY_EXIST = "message.response.warn.username-already-exist";
    public static String INVALID_EMAIL = "message.response.warn.invalid-email";
    public static String INVALID_LOGIN_BODY = "message.response.warn.invalid-login-body";
    public static String EMAIL_IS_NOT_CONFIRMED_YET = "message.response.warn.email-is-not-confirmed-yet";
    public static String INVALID_VERIFICATION_CODE = "message.response.warn.invalid-verification-code";
    public static String INVALID_OLD_PASSWORD = "message.response.warn.invalid-old-password";
    public static String PRODUCT_NOT_EXISTS = "message.response.warn.product-not-exists";
    public static String PRODUCT_NOT_AVAILABLE = "message.response.warn.product-not-available";
    public static String ITEM_NOT_EXISTS_IN_CART = "message.response.warn.item-not-exists-in-cart";
    public static String ITEM_QUANTITY_NOT_EXISTS = "message.response.warn.quantity-not-available";
    public static String FEEDBACK_CANNOT_BE_ADDED = "message.response.warn.feedback-cannot-be-added";


}
