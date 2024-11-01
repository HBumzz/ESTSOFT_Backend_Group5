package com.app.salty.user.controller;

import com.app.salty.user.common.social.KakaoAPI;
import com.app.salty.user.dto.request.UserSignupRequest;
import com.app.salty.user.dto.response.UserResponse;
import com.app.salty.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final KakaoAPI kakaoAPI;

    //로그인 페이지
    @GetMapping("/login")
    public String showLogin(Model model) {
        return "/user/login";  // login.html 반환
    }

    //회원가입 페이지
    @GetMapping("/signup")
    public String showSingup(){
        return "/user/signup";
    }

    @PostMapping("/signup")
    public String createUser(@RequestBody UserSignupRequest request){
        log.info("request: {}", request);
        UserResponse createdUser = userService.signup(request);
        return "redirect:/auth/login";
    }

    //카카오 로그인
    @GetMapping("/login/kakao")
    public String kakaoLogin () {
        String baseUrl = "https://kauth.kakao.com/oauth/authorize";

        return "redirect:" + baseUrl + "?" +
                "client_id=" + kakaoAPI.getClientId() +
                "&redirect_uri=" + kakaoAPI.getRedirectUri() +
                "&response_type=code";
    }

}
