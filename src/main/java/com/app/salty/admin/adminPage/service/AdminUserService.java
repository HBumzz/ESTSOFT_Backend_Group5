package com.app.salty.admin.adminPage.service;

import com.app.salty.admin.adminPage.repository.AdminUserRepository;
import com.app.salty.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;

    // 유저 전체 정보 조회
    public List<Users> getAllUsers() {
        return adminUserRepository.findAll();
    }

    // 닉네임 또는 이메일로 유저 검색
    public Optional<Users> getUserByNicknameOrEmail(String nickname, String email) {
        if (nickname != null) {
            return adminUserRepository.findByNickname(nickname);
        } else if (email != null) {
            return adminUserRepository.findByEmail(email);
        }
        return Optional.empty();
    }

    // 유저 포인트 수정
    public void updateUserPoint(Users user, Long point) {
        user.updatePoint(point);  // 포인트를 입력받은 값으로 수정하는 메서드 호출
        adminUserRepository.save(user);  // 수정된 유저 저장
    }

    // 유저 ID로 조회
    public Optional<Users> getUserById(Long userId) {
        return adminUserRepository.findById(userId);
    }

    // 수정된 유저 정보 저장
    public void save(Users user) {
        adminUserRepository.save(user);  // 유저 정보 저장
    }
}