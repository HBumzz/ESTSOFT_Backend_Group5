package com.app.salty.admin.adminPage.controller;

import com.app.salty.admin.adminPage.service.AdminService;
import com.app.salty.admin.challengeBoard.dto.response.ChallengeViewResponse;
import com.app.salty.admin.challengeBoard.entity.Challenge;
import com.app.salty.admin.challengeBoard.service.ChallengeBoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminPageController {

    public final AdminService adminService;

    public AdminPageController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/admin")
    public String toAdminPage() {

        return "/adminPage/admin";
    }

}
