package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.Message;
import app.backend.click_and_buy.enums.Paths;
import app.backend.click_and_buy.repositories.MessageRepository;
import app.backend.click_and_buy.request.UserMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.eclipse.angus.mail.util.MailConnectException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final ModelMapper modelMapper;
    private final MailingService mailingService;

    public MessageService(MessageRepository messageRepository, ModelMapper modelMapper, MailingService mailingService) {
        this.messageRepository = messageRepository;
        this.modelMapper = modelMapper;
        this.mailingService = mailingService;
    }

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

    public Page<app.backend.click_and_buy.responses.UserMessage> getAll(int page, int size) {
        return messageRepository.findAllByIsReadIsFalse(PageRequest.of(page, size))
                .map(message -> modelMapper.map(message, app.backend.click_and_buy.responses.UserMessage.class));
    }

    public void sendReply(UserMessage message) {
        try {
            mailingService.sendMail(message.getName(), message.getEmail(), message.getMessage(), Paths.REPLY_TO_MESSAGE.getResourcePath(), "Reply to your message");
        }catch (MailConnectException ignored) {
            throw new RuntimeException("Error sending reply");
        }
    }

    public void markAsRead(List<Long> ids) {
        List<Message> messages = messageRepository.findAllById(ids);
        if (messages.isEmpty()) return;
        messages.forEach(message -> {
            message.setIsRead(true);
            message.setUpdatedAt(LocalDateTime.now());
        });
        messageRepository.saveAll(messages);
    }

    public void deleteMessages(List<Long> ids){
        messageRepository.deleteAllById(ids);
    }
}
