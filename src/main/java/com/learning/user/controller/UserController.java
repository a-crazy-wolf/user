package com.learning.user.controller;

import com.learning.user.dto.BaseResponseDTO;
import com.learning.user.dto.UserDto;
import com.learning.user.dto.search.SearchQueryRequest;
import com.learning.user.dto.search.UserListDto;
import com.learning.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

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

    @PostMapping("/search")
    @PreAuthorize("hasAuthority('view')")
    public ResponseEntity<?> getUserList(@RequestBody SearchQueryRequest searchQueryRequest, Locale locale) {
        BaseResponseDTO baseResponseDTO = new BaseResponseDTO();
        try {
            UserListDto userListDto = userService.getUserList(searchQueryRequest);
            baseResponseDTO.setData(userListDto);
            baseResponseDTO.setMessage(HttpStatus.OK.getReasonPhrase());
            return ResponseEntity.status(HttpStatus.OK).body(baseResponseDTO);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(baseResponseDTO);
        }
    }

    @GetMapping("/revision")
    @PreAuthorize("hasAuthority('view')")
    public ResponseEntity<?> getUser(@RequestParam Long userId){
        try {
            List<UserDto> userDtos = userService.getUserPasswordRevisions(userId);
            return ResponseEntity.status(HttpStatus.OK).body(userDtos);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        }
    }
}
