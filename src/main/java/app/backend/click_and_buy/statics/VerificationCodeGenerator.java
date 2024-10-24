package app.backend.click_and_buy.statics;

import static app.backend.click_and_buy.statics.Constants.CHARACTERS;
import static app.backend.click_and_buy.statics.Constants.RANDOM;
import static app.backend.click_and_buy.statics.Constants.CODE_LENGTH;

public class VerificationCodeGenerator {

    private static final int CL= CODE_LENGTH;
    public static String generateVerificationCode() {
        StringBuilder code = new StringBuilder(CL);
        for (int i = 0; i < CL; i++) {
            code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
}
