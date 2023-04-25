package com.learning.user.controller;

import com.learning.user.dto.UserDto;
import com.learning.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('view', 'app')")
    public ResponseEntity<?> getUser(@RequestHeader String userId){
        try {
            Long id = Long.parseLong(userId);
            UserDto userDto = userService.getUser(id);
            return ResponseEntity.status(HttpStatus.OK).body(userDto);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('edit')")
    public ResponseEntity<?> saveUser(@RequestBody UserDto userDto){
        try {
            userService.saveUser(userDto);
            return ResponseEntity.status(HttpStatus.OK).body("Save user successfully!");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        }
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> resetUserPassword(@RequestParam long userId, @RequestParam String password){
        try {
            userService.resetPassword(userId,password);
            return ResponseEntity.status(HttpStatus.OK).body("Reset password successfully!");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        }
    }

}