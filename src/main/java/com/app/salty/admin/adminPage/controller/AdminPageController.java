package com.app.salty.admin.adminPage.controller;

import com.app.salty.admin.adminPage.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Controller
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
