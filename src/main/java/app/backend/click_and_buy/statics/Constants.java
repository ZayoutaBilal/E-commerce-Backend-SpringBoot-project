package app.backend.click_and_buy.statics;

import java.security.SecureRandom;
import java.util.Random;

public class Constants {


    public static final String NUMBERS = "0123456789";
    public static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_?#^$@0123456789";
    public static final int CODE_LENGTH = 6;
    public static final int PASSWORD_LENGTH = 12;
    public static final Random RANDOM = new SecureRandom();

}
