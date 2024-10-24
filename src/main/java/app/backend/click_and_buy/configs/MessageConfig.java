package app.backend.click_and_buy.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageConfig {

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("success", "error", "warning","email-body","email-subject");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}

