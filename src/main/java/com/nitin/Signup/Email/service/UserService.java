package com.nitin.Signup.Email.service;

import com.nitin.Signup.Email.models.User;
import com.nitin.Signup.Email.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> allUsers(){
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add); //Method Reference, either create own Lambda or user preExist function.
        return users;
    }
}
