package com.nitin.Signup.Email.controllers;

import com.nitin.Signup.Email.models.User;
import com.nitin.Signup.Email.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> authenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            String currentUser = String.valueOf(authentication.getPrincipal());
            return ResponseEntity.ok(currentUser);
        }
        catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/allUsers")
    public ResponseEntity<List<User>> allUsers(){
        List<User> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }
}
