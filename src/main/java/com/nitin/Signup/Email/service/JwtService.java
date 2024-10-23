package com.nitin.Signup.Email.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
@Data
public class JwtService {
    // -> We can use @Value() annotation from Spring to get values from external source: application.propertiesss
    private static final SecretKey KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512); /*Jwts.SIG.HS512.key().build();*/
    private static final SecretKey KEY256 = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final int expirationTime = 1000 * 60 * 60; // 1 Hr.

    /* ->    ExtractAllClaims -> Claims -> Username
       -> 1. From token, extract Claims
       -> 2. From Claims, extract username
       (Our Token is in String format)
    */

    /* 1. No Token -> Login(username, password) == UserDetailsService(username, password)
          createToken -> JSON(key:value) -> Giving it to client
       2. There is Token ->
    */

    /* extractAllClaims(String token)
       extractClaim(String token, Function<Claims, T> claimsResolver)

    */
    public static int getExpirationTime(){
        return expirationTime;
    }

    public Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(KEY)
                .build() //-> returned JwtParser(Reads Jwt String -> converts to Jwt Object)
                .parseSignedClaims(token) //-> JWS(expanded version to be used in Java Code)
                .getPayload();

    }

    //Function takes argument:Claims & returns T after performing user defined action.
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims); //-> But we have not defined anonymous function(Lambda Expression) for abstract method, apply.
    }


    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    //-----------private(helper) Methods------------//
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    //----------------------------------------------//

    public boolean isTokenValid(String token, UserDetails userDetails){
        String tokenUsername=extractUsername(token);
        return (tokenUsername.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateToken(String username){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(KEY)
                .compact();
    }
}
