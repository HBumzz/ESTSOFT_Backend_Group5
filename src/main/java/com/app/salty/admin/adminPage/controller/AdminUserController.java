package com.app.salty.admin.adminPage.controller;

import com.app.salty.admin.adminPage.service.AdminUserService;
import com.app.salty.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    // 유저 전체 정보 조회
    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = adminUserService.getAllUsers();
        return ResponseEntity.ok(users);
    }

}