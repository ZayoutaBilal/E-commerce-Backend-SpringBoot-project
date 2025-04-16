package app.backend.click_and_buy.controllers;

import app.backend.click_and_buy.request.UserMessage;
import app.backend.click_and_buy.services.MailingService;
import app.backend.click_and_buy.services.MessageService;
import app.backend.click_and_buy.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Email;
import org.eclipse.angus.mail.util.MailConnectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/")
public class AdminController {

    private final UserService userService;
    private final MessageService messageService;

    public AdminController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @PostMapping("selectAll")
    public ResponseEntity<?> selectAll(){
        return ResponseEntity.ok().body(userService.findAll(true));
    }

    @PostMapping("selectAllDeleted")
    public ResponseEntity<?> selectAllDeleted(){
        return ResponseEntity.ok().body(userService.findAll(true));
    }

    @PostMapping("selectAllNotDeleted")
    public ResponseEntity<?> selectAllNotDeleted(){
        return ResponseEntity.ok().body(userService.findAll(false));
    }

    @GetMapping("messages")
    public ResponseEntity<?> messages(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok().body(messageService.getAll(page, size));
    }
    @PostMapping("messages/send-reply")
    public ResponseEntity<?> sendReply(@RequestBody UserMessage message){
        try {
            System.out.println("Message from controller : " + message);
            messageService.sendReply(message);
            return ResponseEntity.ok().body("The message was sent successfully");
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("messages/mark-as-read/{messageId}")
    public ResponseEntity<?> markAsRead(@PathVariable long messageId){
        try {
            messageService.markAsRead(messageId);
            return ResponseEntity.ok().body("The message was marked as read successfully");
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
