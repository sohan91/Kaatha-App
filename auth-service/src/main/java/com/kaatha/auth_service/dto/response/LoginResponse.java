package com.kaatha.auth_service.dto.response;

import com.kaatha.auth_service.entity.enums.UserType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    private UserType userType;

    private Long shopkeeperId;
}