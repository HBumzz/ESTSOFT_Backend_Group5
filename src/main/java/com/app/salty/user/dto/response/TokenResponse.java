package com.app.salty.user.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@ToString
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String status;
    private String username;
    private boolean rememberMe;

    @Builder
    public TokenResponse(String accessToken, String refreshToken, String status, String username, boolean rememberMe) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.status = status;
        this.username = username;
        this.rememberMe = rememberMe;
    }
}
