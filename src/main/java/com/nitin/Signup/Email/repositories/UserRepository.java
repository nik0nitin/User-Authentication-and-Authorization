package com.nitin.Signup.Email.repositories;

import com.nitin.Signup.Email.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /* -> findByEmail
       -> findByVerificationCode
       When Signup:
            User(findByEmail from Database) == User(findByVerificationCode from Database)
            When this is true. Means Code is entered by same User.
    */

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByVerificationCode(String verificationCode);
}
