package com.nitin.Signup.Email.config;

import com.nitin.Signup.Email.models.User;
import com.nitin.Signup.Email.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
//@Service
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = String.valueOf(authentication.getPrincipal());
        String password = String.valueOf(authentication.getCredentials());

        UserDetails user = userDetailsService.loadUserByUsername(email);
        if(user == null){
            throw new UsernameNotFoundException("User Not Found");
        }
        if(passwordEncoder.matches(password, user.getPassword())){
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    email, password, new ArrayList<>());
            return token;
        }
        else{
            throw new BadCredentialsException("Custom Bad Credential");
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
