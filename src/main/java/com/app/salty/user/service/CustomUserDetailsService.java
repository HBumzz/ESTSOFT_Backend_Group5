package com.app.salty.user.service;

import com.app.salty.user.entity.UserRoleMapping;
import com.app.salty.user.entity.Users;
import com.app.salty.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users currentUser = userRepository.findByEmailWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        log.info("list : {}", currentUser.getUserRoleMappings());

        // 사용자의 최대 권한 레벨 찾기
        int maxLevel = currentUser.getUserRoleMappings().stream()
                .map(mapping -> mapping.getRole().getLevel())
                .max(Integer::compareTo)
                .orElse(0);

        log.info("maxLevel {}:", maxLevel);

        // 해당 레벨 이하의 모든 권한 부여
        Set<GrantedAuthority> authorities = currentUser.getUserRoleMappings().stream()
                .map(UserRoleMapping::getRole)
                .filter(role -> role.getLevel() <= maxLevel)
                .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
                .collect(Collectors.toSet());

        // 디버깅을 위한 상세 로그
        authorities.forEach(auth ->
                log.debug("Authority granted: {}", auth.getAuthority())
        );

        return User.builder()
                .username(currentUser.getEmail())
                .password(currentUser.getPassword())
                .authorities(authorities)
                .build();

    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
