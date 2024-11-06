package com.app.salty.user.service;

import com.app.salty.user.dto.request.LoginRequest;
import com.app.salty.user.dto.response.TokenResponse;
import com.app.salty.user.entity.Users;
import com.app.salty.util.JwtUtil;
import jakarta.mail.AuthenticationFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;


    public TokenResponse authenticateLocal(LoginRequest request) throws AuthenticationFailedException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            log.debug("Authentication successful: {}", authentication);
            return generateAuthResponse(authentication, request.isRememberMe());
        } catch (AuthenticationException e) {
            log.error("Local authentication failed", e);
            throw new AuthenticationFailedException("인증에 실패했습니다.");
        }
    }

    public TokenResponse authenticateKakao(Users socialLoginUser) {
        try {
            Authentication authentication = createAuthenticationToken(socialLoginUser);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
            TokenResponse tokenResponse = generateAuthResponse(authentication, true);
            log.info("tokenResponse : {}", tokenResponse);
            return tokenResponse;
        } catch (Exception e) {
            log.error("Kakao authentication failed", e);
            throw new IllegalArgumentException("카카오 로그인 처리 중 오류가 발생했습니다.");
        }
    }

    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication = createAuthenticationToken(userDetails);

        return generateAuthResponse(authentication, false);
    }

    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            jwtUtil.invalidateToken(jwt);
            SecurityContextHolder.clearContext();
        }
    }

    private Authentication createAuthenticationToken(Users user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        log.info("userDetails ++++++++++++++++++++++++: {}", userDetails);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    private Authentication createAuthenticationToken(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }



    private TokenResponse generateAuthResponse(Authentication authentication, boolean rememberMe) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("authentication : {}",authentication);
        String accessToken = jwtUtil.generateAccessToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(authentication);

        return TokenResponse.builder()
                .status("success")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(authentication.getName())
                .rememberMe(rememberMe)
                .build();
    }
}
