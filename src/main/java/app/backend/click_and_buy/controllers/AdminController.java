package app.backend.click_and_buy.controllers;

import app.backend.click_and_buy.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/")
public class AdminController {

    @Autowired
    private UserService userService;

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


}
