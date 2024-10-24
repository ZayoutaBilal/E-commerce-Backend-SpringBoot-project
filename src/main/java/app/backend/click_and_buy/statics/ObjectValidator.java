package app.backend.click_and_buy.statics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectValidator {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);


    public static boolean stringValidator(String str) {
        return str != null && !str.isBlank() && !str.isEmpty();
    }

    public static boolean emailValidator(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
