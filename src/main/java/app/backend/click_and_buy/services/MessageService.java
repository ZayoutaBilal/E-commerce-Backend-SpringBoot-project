package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.Message;
import app.backend.click_and_buy.repositories.MessageRepository;
import app.backend.click_and_buy.request.UserMessage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public boolean save(UserMessage message) {
        try {
            messageRepository.save(Message.builder()
                    .email(message.getEmail())
                    .name(message.getName())
                    .message(message.getMessage())
                    .build());
            return true;
        } catch (Exception ignored) {return false;}
    }
}
