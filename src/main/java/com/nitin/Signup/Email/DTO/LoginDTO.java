package com.nitin.Signup.Email.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Data
public class LoginDTO {
    private String email; //-> Can use username/email for login.
    private String password;
}
