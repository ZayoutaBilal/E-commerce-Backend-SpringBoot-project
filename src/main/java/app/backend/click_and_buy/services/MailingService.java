package app.backend.click_and_buy.services;

import app.backend.click_and_buy.enums.Paths;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.eclipse.angus.mail.util.MailConnectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.nio.charset.StandardCharsets;

@Service
public class MailingService {


    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public MailingService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @SneakyThrows
    public void sendMail(String var, String recipientEmail, String message, String template, String subject)  throws MailConnectException{
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        Context context = new Context();
        context.setVariable("var", var);
        context.setVariable("message", message);
        String htmlContent = templateEngine.process(template, context);
        helper.setText(htmlContent, true);
        helper.addInline("logo", new ClassPathResource(Paths.LOGO.getResourcePath()));

        javaMailSender.send(mimeMessage);

    }
}
