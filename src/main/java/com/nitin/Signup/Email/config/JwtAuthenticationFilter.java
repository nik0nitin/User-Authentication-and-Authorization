package com.nitin.Signup.Email.config;

import com.nitin.Signup.Email.service.JwtService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @Override
    public void doFilterInternal(@NonNull HttpServletRequest request,
                                 @NonNull HttpServletResponse response,
                                 @Nonnull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        //-> if Authorization Header is Missing
        if(authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.isEmpty()){
            filterChain.doFilter(request,response);
            return;
        }
        //-> if Authorization Header is There. Token is valid & !valid.
        String token = authHeader.split(" ")[1];
        String tokenUsername = jwtService.extractUsername(token); //Let's try with Email
        UserDetails userDetails = userDetailsService.loadUserByUsername(tokenUsername);
        if(!jwtService.isTokenValid(token, userDetails)){
            filterChain.doFilter(request,response);
            return;
        }


        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(), null, userDetails.getAuthorities());

            /* According to Internet, setDetails() stores IP other Info.
             */
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request,response);
        }
        catch (Exception e){
            handlerExceptionResolver.resolveException(request,response,null, e);
        }
    }
}
