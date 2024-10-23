package com.nitin.Signup.Email.controllers;

import com.nitin.Signup.Email.DTO.LoginDTO;
import com.nitin.Signup.Email.DTO.SignupDTO;
import com.nitin.Signup.Email.DTO.VerifyUserDTO;
import com.nitin.Signup.Email.models.User;
import com.nitin.Signup.Email.responses.LoginResponse;
import com.nitin.Signup.Email.service.AuthenticationService;
import com.nitin.Signup.Email.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupDTO signupDTO){
        try {
            User registeredUser = authenticationService.signup(signupDTO);
            return ResponseEntity.ok(registeredUser);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO){
        try {
            User authenticatedUser = authenticationService.authenticate(loginDTO);
            System.out.println(authenticatedUser.getUsername() + " " + authenticatedUser.getEmail());
            String jwtToken = jwtService.generateToken(authenticatedUser.getEmail());
            LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
            return ResponseEntity.ok(loginResponse);
        }catch (RuntimeException e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDTO verifyUserDTO){
        try{
            authenticationService.verifyUser(verifyUserDTO);
            return ResponseEntity.ok("Account Verified Successfully");
        }
        catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email){
        try{
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification Code Sent");
        }
        catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
