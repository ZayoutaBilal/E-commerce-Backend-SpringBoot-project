package app.backend.click_and_buy.services;

import app.backend.click_and_buy.statics.VerificationCodeGenerator;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PasswordVerificationCodeService {


    public String generateVerificationCode() {
        return VerificationCodeGenerator.generateVerificationCode();
    }

    @Cacheable(cacheNames = "passwordVerificationCodes", key = "#email")
    public String getVerificationCode(String email) {
        return null;
    }

    @CacheEvict(cacheNames = "passwordVerificationCodes", key = "#email")
    public void removeVerificationCode(String email) {

    }

    @Cacheable(cacheNames = "passwordVerificationCodes", key = "#email")
    public String storeVerificationCode(String email) {
        return generateVerificationCode();
    }
}
