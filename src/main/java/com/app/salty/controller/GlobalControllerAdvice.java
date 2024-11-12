package com.app.salty.controller;

import com.app.salty.user.entity.CustomUserDetails;
import com.app.salty.user.entity.Users;
import com.app.salty.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addUserInfo(Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails != null) {
            model.addAttribute("user", customUserDetails);

            Users user = userRepository.findById(customUserDetails.getId()).orElse(null);
            if (user != null) {
                model.addAttribute("userPoint", user.getPoint());
            }
        }
    }

    @ModelAttribute
    public void addRequestURI(HttpServletRequest request, Model model) {
        model.addAttribute("requestURI", request.getRequestURI());
    }
}
