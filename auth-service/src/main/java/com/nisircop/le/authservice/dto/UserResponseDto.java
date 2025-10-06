package com.nisircop.le.authservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String phone;
    private String badgeNumber;
    private Long createdBy;
    private boolean isActive;
}