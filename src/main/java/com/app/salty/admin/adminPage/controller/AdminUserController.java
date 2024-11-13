package com.app.salty.admin.adminPage.controller;

import com.app.salty.admin.adminPage.dto.request.UserRoleUpdateRequest;
import com.app.salty.admin.adminPage.repository.AdminUserRepository;
import com.app.salty.admin.adminPage.service.AdminUserService;
import com.app.salty.user.common.Role;
import com.app.salty.user.entity.Roles;
import com.app.salty.user.entity.UserRoleMapping;
import com.app.salty.user.entity.Users;
import com.app.salty.user.repository.RolesRepository;
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
    private final AdminUserRepository adminUserRepository;

    private final RolesRepository rolesRepository;

    @PutMapping("/roles")
    public ResponseEntity<Object> updateUserRoles(@RequestBody UserRoleUpdateRequest request) {

        Users found = adminUserRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("not found"));

        if(request.getRoles() == null || request.getRoles().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<UserRoleMapping> newRoles = request.getRoles().stream().map(s -> {
            Roles role = rolesRepository.findByRole(Role.valueOf(s)).get();
            return UserRoleMapping.builder().user(found).role(role).build();
        }).toList();

        found.updateUsersMapping(newRoles);
        adminUserRepository.save(found);

        return ResponseEntity.ok().build();
    }


}