package com.nisircop.le.authservice.service;

import com.nisircop.le.authservice.client.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserServiceClient userServiceClient;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            UserServiceClient.UserDTO userDTO = userServiceClient.validateUser(new UserServiceClient.ValidateRequest(username, password));

            if (userDTO != null) {
                return new UsernamePasswordAuthenticationToken(
                        username,
                        password,
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_" + userDTO.role()))
                );
            } else {
                throw new BadCredentialsException("Invalid username or password");
            }
        } catch (Exception e) {
            // This will catch Feign exceptions if the user-service returns 401
            throw new BadCredentialsException("Invalid username or password", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}