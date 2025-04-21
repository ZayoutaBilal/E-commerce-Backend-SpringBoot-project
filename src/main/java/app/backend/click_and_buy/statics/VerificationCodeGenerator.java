package app.backend.click_and_buy.statics;

import static app.backend.click_and_buy.statics.Constants.*;

public class VerificationCodeGenerator {

    private static final int CL= CODE_LENGTH;
    private static final int PL= PASSWORD_LENGTH;

    public static String generateVerificationCode() {
        StringBuilder code = new StringBuilder(CL);
        for (int i = 0; i < CL; i++) {
            code.append(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));
        }
        return code.toString();
    }

    public static String generatePassword() {
        StringBuilder password = new StringBuilder(PL);
        for (int i = 0; i < PL; i++) {
            password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }
}
