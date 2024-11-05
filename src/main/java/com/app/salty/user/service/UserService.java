package com.app.salty.user.service;


import com.app.salty.config.globalExeption.custom.DuplicateEmailException;
import com.app.salty.user.common.Role;
import com.app.salty.user.dto.request.UserSignupRequest;
import com.app.salty.user.dto.response.UserResponse;
import com.app.salty.user.entity.Roles;
import com.app.salty.user.entity.UserRoleMapping;
import com.app.salty.user.entity.Users;
import com.app.salty.user.repository.RolesRepository;
import com.app.salty.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse signup(UserSignupRequest request) {
        validateDuplicateEmail(request.getEmail());


        Users signupUser = userSignupRequestToUser(request);
        Roles userRole = rolesRepository.findByRole(Role.USER)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 권한입니다."));

        UserRoleMapping userRoleMapping = new UserRoleMapping(signupUser,userRole);
        signupUser.addRoleMappings(userRoleMapping);

        Users savedUser = userRepository.save(signupUser);
        log.info("Saved user: {}", savedUser);

        return convertUserResponse(savedUser);
    }

    //convert
    private Users userSignupRequestToUser(UserSignupRequest request) {
        return Users.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .activated(true)
                .build();
    }

    private UserResponse convertUserResponse(Users user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    //이메일 중복 검사
    private void validateDuplicateEmail(String email) {
        if(userRepository.existsByEmail(email))
            throw new DuplicateEmailException("이미 가입된 이메일입니다");
    }

    public boolean verifyNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    //빌드시
    @PostConstruct
    public void initRoles() {
        if (rolesRepository.count() == 0) {
            List<Roles> roles = Arrays.asList(
                    new Roles(Role.USER,"기본 사용자 권한",1),
                    new Roles(Role.USER2,"레벨2 사용자 권한",2),
                    new Roles(Role.USER3,"레벨3 사용자 권한",3),
                    new Roles(Role.USER4,"레벨5 사용자 권한",4),
                    new Roles(Role.ADMIN,"관리자 권한",5)
            );
            rolesRepository.saveAll(roles);
        }
    }

}
