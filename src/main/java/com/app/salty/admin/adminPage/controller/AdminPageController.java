package com.app.salty.admin.adminPage.controller;

import com.app.salty.admin.adminPage.service.AdminUserService;
import com.app.salty.user.entity.Users;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;


@Controller
public class AdminPageController {

    public final AdminUserService adminUserService;

    public AdminPageController(AdminUserService adminService) {
        this.adminUserService = adminService;
    }

    @GetMapping("/admin")
    public String getAllUsersPage(Model model) {
        List<Users> users = adminUserService.getAllUsers();
        model.addAttribute("users", users);  // users 데이터를 모델에 추가
        return "adminPage/admin";  // 반환할 템플릿 파일 이름 (admin/users.html)
    }

    // 닉네임 또는 이메일로 유저 개별 정보 조회
    @GetMapping("/admin/users/search")
    public String getUserByNicknameOrEmail(@RequestParam(required = false) String nickname,
                                           @RequestParam(required = false) String email,
                                           Model model) {
        Optional<Users> user = adminUserService.getUserByNicknameOrEmail(nickname, email);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "adminPage/userDetail";  // userDetail.html로 이동
        } else {
            model.addAttribute("error", "유저를 찾을 수 없습니다.");
            return "adminPage/admin";  // 유저가 없으면 에러 메시지와 함께 어드민 페이지로 이동
        }
    }


    @PostMapping("/admin/users/update")
    public String updateUser(@RequestParam Long userId,
                             @RequestParam Long point,
                             @RequestParam boolean activated,
                             Model model) {
        // 유저 ID로 유저 조회
        Optional<Users> userOptional = adminUserService.getUserById(userId);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();

            // 포인트와 활성화 상태 수정
            user.updatePoint(point);  // 포인트 수정
            user.updateActivated(activated);  // 활성화 상태 수정

            // 수정된 유저 정보 저장
            adminUserService.save(user);

            // 수정된 유저 정보 다시 모델에 추가
            model.addAttribute("user", user);
            return "adminPage/userDetail";  // 수정된 유저 정보를 보여주는 페이지로 리턴
        } else {
            model.addAttribute("error", "유저를 찾을 수 없습니다.");
            return "adminPage/admin";  // 유저를 찾을 수 없을 경우 에러 메시지 표시
        }
    }

}
