package com.kaatha.auth_service.dto.request;

import com.kaatha.auth_service.entity.enums.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendOtpRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$",
            message = "Invalid phone number")
    private String phoneNumber;

    @NotNull(message = "User type is required")
    private UserType userType;

    private UserType forRegistration;
}