package app.backend.click_and_buy.massages;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//@Getter
//@Setter
//@Configuration
//@ConfigurationProperties("message.response.success")
public class Success {



    public static String USER_SIGNUP = "message.response.success.user-signup";
    public static String VERIFICATION_CODE_SENT = "message.response.success.verification-code-sent";
    public static String VERIFICATION_CODE_VALID = "message.response.success.verification-code-valid";
    public static String PASSWORD_UPDATED = "message.response.success.password-updated";
    public static String USERNAME_UPDATED = "message.response.success.username-updated";
    public static String PICTURE_UPLOADED = "message.response.success.picture-uploaded";
    public static String CUSTOMER_INFOS_UPDATED = "message.response.success.customer-infos-updated";
    public static String ACCOUNT_REMOVED = "message.response.success.account-removed";
    public static String EMAIL_CONFIRMED = "message.response.success.email-confirmed";
    public static String PRODUCT_HAS_BEEN_ADDED_TO_CART = "message.response.success.product-has-been-added-to-cart";
    public static String ITEM_DELETED_FROM_CART = "message.response.success.item-has-been-deleted-from-cart";
    public static String ITEM_QUANTITY_UPDATED = "message.response.success.item-quantity-has-been-updated";
    public static String FEEDBACK_ADDED = "message.response.success.feedback-added";
    public static String USER_SIGN_IN = "message.response.success.user-sign-in";
    public static String USER_MESSAGE = "message.response.success.user-message";
    public static String ADD_CATEGORY = "message.response.success.add-category";
    public static String DELETE_CATEGORY = "message.response.success.delete-category";
    public static String UPDATE_CATEGORY = "message.response.success.update-category";


}
