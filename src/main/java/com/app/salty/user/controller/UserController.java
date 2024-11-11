package com.app.salty.user.controller;

import com.app.salty.user.common.social.KakaoAPI;
import com.app.salty.user.dto.request.UserUpdateRequest;
import com.app.salty.user.dto.request.UserSignupRequest;
import com.app.salty.user.dto.response.TokenResponse;
import com.app.salty.user.dto.response.UserResponse;
import com.app.salty.user.dto.response.UsersResponse;
import com.app.salty.user.entity.CustomUserDetails;
import com.app.salty.user.entity.Users;
import com.app.salty.user.service.AuthenticationService;
import com.app.salty.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final KakaoAPI kakaoAPI;

    //로그인 페이지
    @GetMapping("/login")
    public String showLogin(Model model) {

        return "/user/login";  // login.html 반환
    }

    //회원가입 페이지
    @GetMapping("/signup")
    public String showSingup() {
        return "/user/signup";
    }

    @PostMapping("/signup")
    public String createUser(@RequestBody UserSignupRequest request) {
        log.info("request: {}", request);
        UserResponse createdUser = userService.signup(request);
        return "/user/login";
    }

    //카카오 로그인
    @GetMapping("/login/kakao")
    public String kakaoLogin() {
        String baseUrl = "https://kauth.kakao.com/oauth/authorize";

        return "redirect:" + baseUrl + "?" +
                "client_id=" + kakaoAPI.getClientId() +
                "&redirect_uri=" + kakaoAPI.getRedirectUri() +
                "&response_type=code";
    }

    //kakao callback
    @GetMapping("/kakao/callback")
    public String kakaoCallback(@RequestParam String code,
                                Model model,
                                HttpSession session) {

        Users socialLoginUser = userService.kakaoLogin(code);
        TokenResponse tokenResponse = authenticationService.authenticateKakao(socialLoginUser);
        model.addAttribute("tokenResponse", tokenResponse);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return "redirect:/";
    }

    //프로필 페이지
    @GetMapping("/profile")
    public String updateUser(Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        UsersResponse usersResponse = userService.findByUserWithProfile(currentUser);
        model.addAttribute("user", usersResponse);
        log.info("User: {}", usersResponse);

        return "/user/profile";
    }

    //출석체크 페이지
    @GetMapping("/attendance")
    public String attendance(Model model, @AuthenticationPrincipal UserDetails currentUser) {

        return "/user/attendance";
    }

    //유저 상세 페이지
    @GetMapping("/userDetails")
    public String userDetails(Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        UsersResponse usersResponse = userService.findByUserWithProfile(currentUser);
        model.addAttribute("user", usersResponse);

        return "/user/userDetails";
    }

}
