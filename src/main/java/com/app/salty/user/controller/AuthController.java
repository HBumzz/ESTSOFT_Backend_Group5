package com.app.salty.user.controller;

import com.app.salty.user.dto.request.EmailVerificationRequest;
import com.app.salty.user.dto.request.LoginRequest;
import com.app.salty.user.dto.kakao.KAKAOAuthResponse;
import com.app.salty.user.dto.response.TokenResponse;
import com.app.salty.user.entity.Users;
import com.app.salty.user.service.EmailService;
import com.app.salty.user.service.UserService;
import com.app.salty.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final UserService userService;

    //로컬 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtUtil.generateAccessToken(authentication);
            String refreshToken = jwtUtil.generateRefreshToken(authentication);

            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.setStatus("success");
            tokenResponse.setAccessToken(accessToken);
            tokenResponse.setRefreshToken(refreshToken);
            tokenResponse.setUsername(authentication.getName());
            tokenResponse.setRememberMe(loginRequest.isRememberMe());

            return ResponseEntity.ok(tokenResponse);
        } catch (AuthenticationException e) {
            log.error("Authentication failed: ", e);
            TokenResponse errorResponse = new TokenResponse();
            errorResponse.setStatus("error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    //kakao 로그인
    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
        System.out.println("핸들러 작동 확인 +++++++++++++++++++++++");
        log.info("code : {}",code);
        // 인증 코드로 카카오 로그인 처리
        Users authResponse = userService.kakaoLogin(code);
        return ResponseEntity.ok(authResponse);
    }



    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        try {
            // 리프레시 토큰 검증
            if (!jwtUtil.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }

            // 사용자 정보 추출
            String username = jwtUtil.extractUsername(refreshToken);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 새로운 액세스 토큰 발급
            String newAccessToken = jwtUtil.generateAccessToken(authentication);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", newAccessToken);

            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            log.error("Token refresh failed: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token refresh failed");
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String accessToken) {
        try {
            // 토큰 검증
            if (accessToken != null && accessToken.startsWith("Bearer ")) {
                String token = accessToken.substring(7);
                // 토큰 블랙리스트 처리 또는 무효화
                jwtUtil.invalidateToken(token);
            }

            SecurityContextHolder.clearContext();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Logout failed: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
