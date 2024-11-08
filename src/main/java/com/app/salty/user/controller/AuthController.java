package com.app.salty.user.controller;

import com.app.salty.user.dto.request.EmailVerificationRequest;
import com.app.salty.user.dto.request.LoginRequest;
import com.app.salty.user.dto.kakao.KAKAOAuthResponse;
import com.app.salty.user.dto.response.TokenResponse;
import com.app.salty.user.entity.Users;
import com.app.salty.user.service.AuthenticationService;
import com.app.salty.user.service.CustomUserDetailsService;
import com.app.salty.user.service.EmailService;
import com.app.salty.user.service.UserService;
import com.app.salty.util.JwtUtil;
import jakarta.mail.AuthenticationFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    //로컬 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            TokenResponse tokenResponse = authenticationService.authenticateLocal(loginRequest);
            log.info("tokenResponse : {}", tokenResponse);

            return ResponseEntity.ok(tokenResponse);
        } catch (AuthenticationFailedException e) {
            System.out.println("local login failed : /login" + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        TokenResponse newResponseToken = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok(newResponseToken);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String accessToken) {
        authenticationService.logout(accessToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email-verification-send")
    public ResponseEntity<Boolean> sendVerificationEmail(@RequestParam String email , Model model) {
        String code = emailService.sendVerificationEmail(email);
        log.info("code: {}", code);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email-verification")
    public ResponseEntity<Boolean> verifyEmail(@RequestBody EmailVerificationRequest request)
    {
        boolean verification =emailService.verifyCode(request.getEmail(),request.getCode());
        return ResponseEntity.ok(verification);
    }

    @PostMapping("/nickname-verification")
    public ResponseEntity<Boolean> verifyNickname (@RequestParam String nickname){
        boolean isAvailable = userService.verifyNickname(nickname);
        return ResponseEntity.ok(isAvailable);
    }

}
