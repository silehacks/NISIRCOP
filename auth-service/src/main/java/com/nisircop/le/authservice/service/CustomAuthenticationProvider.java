package com.nisircop.le.authservice.service;

import com.nisircop.le.authservice.client.UserServiceClient;
import com.nisircop.le.authservice.dto.UserDTO;
import com.nisircop.le.authservice.dto.UserResponseDto;
import com.nisircop.le.authservice.dto.ValidateRequest;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserServiceClient userServiceClient;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            ValidateRequest validateRequest = new ValidateRequest(username, password);
            ResponseEntity<UserResponseDto> responseEntity = userServiceClient.validateUser(validateRequest);

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.hasBody()) {
                UserResponseDto userDto = responseEntity.getBody();
                if (userDto != null && userDto.getRole() != null) {
                    // Convert UserResponseDto to UserDTO for consistency
                    UserDTO userDTO = new UserDTO();
                    userDTO.setId(userDto.getId());
                    userDTO.setUsername(userDto.getUsername());
                    userDTO.setRole(userDto.getRole());
                    
                    // Store the entire DTO as the principal for later use
                    return new UsernamePasswordAuthenticationToken(
                            userDTO,
                            password,
                            Collections.singleton(new SimpleGrantedAuthority("ROLE_" + userDto.getRole()))
                    );
                }
            }
            // If the response was not successful or had no body, credentials are bad
            throw new BadCredentialsException("Invalid username or password");

        } catch (FeignException e) {
            // If the user-service returns a non-2xx status (like 401), it will throw a FeignException
            throw new BadCredentialsException("Invalid username or password", e);
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            throw new BadCredentialsException("Authentication service encountered an error", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}