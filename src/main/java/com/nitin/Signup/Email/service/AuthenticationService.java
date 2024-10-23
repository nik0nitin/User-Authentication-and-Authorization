package com.nitin.Signup.Email.service;

import com.nitin.Signup.Email.DTO.LoginDTO;
import com.nitin.Signup.Email.DTO.SignupDTO;
import com.nitin.Signup.Email.DTO.VerifyUserDTO;
import com.nitin.Signup.Email.models.User;
import com.nitin.Signup.Email.repositories.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    /* UserRepository -> find user by email, verificationCode
        UserDetails (Loads user details)
            |
           User

       PasswordEncoder -> two functions(encode, match)
       User Credentials -> encoded == encoded password <- Database

       AuthenticationManager, EmailService
    */

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public User signup(SignupDTO input){
        //-> 1 Step missing -> Check in Database if user already exists.
        boolean userPreExists = userRepository.findByEmail(input.getEmail())
                .isPresent();
        if(userPreExists){
            throw new RuntimeException("User Already Exists");
        }
        User user = new User(input.getUsername(), input.getEmail(), passwordEncoder.encode(input.getPassword()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiration(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    //-> When User Logs in -> username == username(database) && encoded.(password) == password(database)
    public User authenticate(LoginDTO input){
        User user = userRepository.findByEmail(input.getEmail())
                 .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));

         //-> if User exists, then? enabled or !enabled
        if(!user.isEnabled()){
            throw new RuntimeException("Account not verified. Please verify your account");
        }

        //-> enabled(user has verified) -> right password?
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                input.getEmail(), input.getPassword());

        //-> AFAI Understand authentication Object won't be authenticated for now.
        authenticationManager.authenticate(token); //<- Having issues when login.
        return user;
    }

    //Approach with Conditional Statements if(true) whereas usually in Spring(FilterChain) -> if(!true)
    public void verifyUser(VerifyUserDTO input){
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(user.getVerificationCodeExpiration().isBefore(LocalDateTime.now())){
                throw new RuntimeException("Verification Code Expired");
            }

            if(user.getVerificationCode().equals(input.getVerificationCode())){
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiration(null);
                userRepository.save(user);
            }
            else{
                throw new RuntimeException("Invalid Verification Code");
            }
        }
        else{
            throw new RuntimeException("User Not Found");
        }
    }

    public void resendVerificationCode(String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(user.isEnabled()){
                throw new RuntimeException("Account Is Already Verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiration(LocalDateTime.now().plusMinutes(15));
            sendVerificationEmail(user);
            userRepository.save(user);
        }
        else{
            throw new RuntimeException("User Not Found");
        }
    }

    public void sendVerificationEmail(User user){
        String subject = "Account Verification";
        String verificationCode = user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateVerificationCode(){
        Random random = new Random();
        int code = random.nextInt(900000) + 1_00_000; //-> Bigger than 1_00_000 to get in 6 digit range -- Also the upper bound is not inclusive
        return String.valueOf(code);
    }

}
