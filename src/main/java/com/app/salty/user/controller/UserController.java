package com.app.salty.user.controller;

import com.app.salty.user.common.social.KakaoAPI;
import com.app.salty.user.dto.request.UserSignupRequest;
import com.app.salty.user.dto.response.TokenResponse;
import com.app.salty.user.dto.response.UserResponse;
import com.app.salty.user.entity.Users;
import com.app.salty.user.service.AuthenticationService;
import com.app.salty.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
                                Model model) {

        Users socialLoginUser = userService.kakaoLogin(code);
        TokenResponse tokenResponse= authenticationService.authenticateKakao(socialLoginUser);
        model.addAttribute("tokenResponse", tokenResponse);
        return "redirect:/";
    }

}
