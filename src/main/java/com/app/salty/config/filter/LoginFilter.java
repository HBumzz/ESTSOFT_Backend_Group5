package com.app.salty.config.filter;

import com.app.salty.user.dto.response.TokenResponse;
import com.app.salty.user.entity.CustomUserDetails;
import com.app.salty.util.JwtUtil;
import com.app.salty.user.dto.request.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // 로그인 요청 정보 파싱
            LoginRequest loginRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginRequest.class);
             log.info("loginRequest: 시작 {}", loginRequest);

            // 인증 객체 생성
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    );

            setDetails(request, authToken);

            // AuthenticationManager가 CustomUserDetailsService를 사용하여 인증 처리
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {

        System.out.println("로그인 필터 성공");
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();

        // Access Token과 Refresh Token 생성
        String accessToken = jwtUtil.generateAccessToken(authResult);
        String refreshToken = jwtUtil.generateRefreshToken(authResult);

        TokenResponse tokenResponse = TokenResponse.builder()
                .status("success")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(userDetails.getUsername())
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 응답 헤더에 토큰 추가
        new ObjectMapper().writeValue(response.getOutputStream(), tokenResponse);

        log.debug("Authentication successful for user: {}", userDetails.getUsername());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) {
        // 인증 실패 처리

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    }

}
