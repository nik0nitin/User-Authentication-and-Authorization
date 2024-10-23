package com.nitin.Signup.Email.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyUserDTO {
    private String email;
    private String verificationCode;
}
