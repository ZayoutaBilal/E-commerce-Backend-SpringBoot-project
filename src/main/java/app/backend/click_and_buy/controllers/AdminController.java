package app.backend.click_and_buy.controllers;

import app.backend.click_and_buy.enums.Roles;
import app.backend.click_and_buy.request.MessageAction;
import app.backend.click_and_buy.request.UserManagement;
import app.backend.click_and_buy.request.UserMessage;
import app.backend.click_and_buy.responses.UserInfos;
import app.backend.click_and_buy.services.CustomerService;
import app.backend.click_and_buy.services.MessageService;
import app.backend.click_and_buy.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/")
public class AdminController {

    private final UserService userService;
    private final CustomerService customerService;
    private final MessageService messageService;

    public AdminController(UserService userService, CustomerService customerService, MessageService messageService) {
        this.userService = userService;
        this.customerService = customerService;
        this.messageService = messageService;
    }

    @GetMapping("messages")
    public ResponseEntity<?> messages(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok().body(messageService.getAll(page, size));
    }
    @PostMapping("messages/{messageId}/send-reply")
    public ResponseEntity<?> sendReply(@PathVariable Long messageId, @RequestBody UserMessage message){
        try {
            messageService.sendReply(message);
            messageService.markAsRead(List.of(messageId));
            return ResponseEntity.ok().body("The message was sent successfully");
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("messages")
    public ResponseEntity<?> markAsReadAndDelete(@RequestBody MessageAction messageAction){
        try {
            if(!messageAction.getIdsToMarkAsRead().isEmpty()) messageService.markAsRead(messageAction.getIdsToMarkAsRead());
            if(!messageAction.getIdsToDelete().isEmpty()) messageService.deleteMessages(messageAction.getIdsToDelete());
            return ResponseEntity.ok().body("The messages were marked as read successfully");
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("customers-services")
    public ResponseEntity<?> getCustomersServices(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size){
        try {
            Page<UserInfos> userInfosPage = customerService.get(page, size, Roles.CUSTOMER_SERVICE);
            return ResponseEntity.ok().body(userInfosPage);
        }catch (RuntimeException exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    @GetMapping("customers")
    public ResponseEntity<?> getCustomers(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size){
        try {
            Page<UserInfos> userInfosPage = customerService.get(page, size, Roles.CUSTOMER);
            return ResponseEntity.ok().body(userInfosPage);
        }catch (RuntimeException exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    @PostMapping("add-user")
    public ResponseEntity<?> addUser(@RequestBody UserManagement userManagement){
        try {
            String password = customerService.addCustomerOrCustomerService(userManagement);
            return ResponseEntity.ok().body("User has been added successfully + '"+password+"'");
        }catch (RuntimeException exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    @PostMapping("{userId}/update-user")
    public ResponseEntity<?> addUser(@PathVariable long userId,@RequestBody UserManagement userManagement){
        try {
            customerService.editCustomerOrCustomerService(userId,userManagement);
            return ResponseEntity.ok().body("User has been updated successfully");
        }catch (RuntimeException exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    @PutMapping("{userId}/reset-user-password")
    public ResponseEntity<?> resetPassword(@PathVariable long userId){
        try {
            return ResponseEntity.ok().body(customerService.resetPassword(userId));
        }catch (RuntimeException exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }


}
