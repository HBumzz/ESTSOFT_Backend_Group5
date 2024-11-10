package com.app.salty.user.controller;

import com.app.salty.user.dto.request.EmailVerificationRequest;
import com.app.salty.user.dto.request.LoginRequest;
import com.app.salty.user.dto.request.UserUpdateRequest;
import com.app.salty.user.dto.response.AttendanceResponse;
import com.app.salty.user.dto.response.TokenResponse;
import com.app.salty.user.dto.response.UsersResponse;
import com.app.salty.user.entity.CustomUserDetails;
import com.app.salty.user.service.AuthenticationService;
import com.app.salty.user.service.EmailService;
import com.app.salty.user.service.UserService;
import com.app.salty.util.JwtUtil;
import jakarta.mail.AuthenticationFailedException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest,
                                               HttpSession session) {
        try {
            TokenResponse tokenResponse = authenticationService.authenticateLocal(loginRequest);
            log.info("tokenResponse : {}", tokenResponse);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            return ResponseEntity.ok(tokenResponse);
        } catch (AuthenticationFailedException e) {
            System.out.println("local login failed : /login" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestParam("profileImage") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) throws IOException {

        if(file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "파일 크기는 10MB를 초과할 수 없습니다."
            ));
        }

        String imageUrl = userService.updateProfileImage(
                currentUser.getUsername(),  // email
                file
        );
        log.info("imageUrl : {}", imageUrl);

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "프로필 변경을 완료했습니다."
        ));
    }

    //프로필 수정
    @PostMapping("/userUpdate")
    public ResponseEntity<Map<String, Object>> profileUpdate(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestBody UserUpdateRequest userUpdateRequest,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Received userUpdateRequest: {}", userUpdateRequest); // 로그 추가
        log.info("Form data - nickname: {}, bio: {}",
                userUpdateRequest.getNickname(),
                userUpdateRequest.getBio());     // 상세 로그 추가
        UsersResponse usersResponse = userService.updateProfile(currentUser.getUsername(),userUpdateRequest);


        return ResponseEntity.ok().body(Map.of(
                "nickname", usersResponse.getNickname(),
                "bio", usersResponse.getBio()
        ));
    }

    //출석리스트
    @GetMapping("/attendance")
    public ResponseEntity<AttendanceResponse> getAttendanceList(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ){
        AttendanceResponse response = userService.findByUserWithAttendance(currentUser.getId());

        return ResponseEntity.ok().body(response);
    }

    //출석체크
    @PostMapping("/attendance.do")
    public ResponseEntity<AttendanceResponse> attendance(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ){
        AttendanceResponse response = userService.updateUserAttendance(currentUser.getId());

        return ResponseEntity.ok(response);
    }



    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        TokenResponse newResponseToken = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok(newResponseToken);
    }

    // 로그아웃 토큰 방식 시
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
