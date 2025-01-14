package com.app.salty.controller;

import com.app.salty.user.dto.response.UsersResponse;
import com.app.salty.user.entity.CustomUserDetails;
import com.app.salty.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeViewController {

    private final UserService userService;

    @GetMapping()
    public String showIndex(
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if(userDetails != null) {
            log.info("UserDetails: {}", userDetails);
            UsersResponse usersResponse = userService.findByUserWithProfile(userDetails);
            model.addAttribute("user", usersResponse);
            log.info("User: {}", usersResponse);
            log.info("Profile: {}", usersResponse.getProfile());
        }

        return "index";
    }
    @GetMapping("/error/403")
    public String showError() {
        return "error/403";
    }

}
