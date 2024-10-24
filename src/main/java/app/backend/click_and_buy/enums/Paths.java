package app.backend.click_and_buy.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Paths {

    LOGO("/images/logo_click_and_buy.ico"),
    TEMPLATE_EMAIL_SIGNUP("email_signup_success.html"),
    TEMPLATE_CONFIRM_EMAIL("email_confirm_addressEmail.html"),
    TEMPLATE_FORGET_PASSWORD("email_forget_password.html"),
    TEMPLATE_MESSAGE_TO_USER("email_message_to_user.html"),
    TEMPLATES_PATH("classpath:/emailTemplates/html/");

    private String resourcePath;
}
