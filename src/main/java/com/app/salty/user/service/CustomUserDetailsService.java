package com.app.salty.user.service;

import com.app.salty.user.entity.CustomUserDetails;
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
        log.info("currentUser: {}", currentUser);
        return createUserDetails(currentUser);
    }

    private CustomUserDetails createUserDetails(Users user) {
        log.info("createUserDetails: {}", getAuthorities(user));
        Set<GrantedAuthority> authorities = getAuthorities(user);

        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .authorities(authorities)
                .build();

//        return User.builder()
//                .username(user.getEmail())
//                .password(user.getPassword())
//                .authorities(authorities)
//                .build();
    }

    private Set<GrantedAuthority> getAuthorities(Users user) {
        log.info("getUserRoleMappings+++++++++++: {}", user.getUserRoleMappings());
//        int maxLevel = user.getUserRoleMappings().stream()
//                .map(mapping -> mapping.getRole().getLevel())
//                .max(Integer::compareTo)
//                .orElse(0);
//        log.info("maxLevel: {}", maxLevel);
        return user.getUserRoleMappings().stream()
                .map(UserRoleMapping::getRole)
                .map(role -> {
                    String authority = "ROLE_" + role.getRole().name();
                    log.info("Adding authority: {}", authority);
                    return new SimpleGrantedAuthority(authority);
                })
                .collect(Collectors.toSet());
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
