package com.app.salty.user.service;

import com.app.salty.user.dto.response.TokenResponse;
import com.app.salty.user.entity.UserRoleMapping;
import com.app.salty.user.entity.Users;
import com.app.salty.user.repository.UserRepository;
import com.app.salty.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

        return createUserDetails(currentUser);
    }

    private UserDetails createUserDetails(Users user) {
        Set<GrantedAuthority> authorities = getAuthorities(user);

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }

    private Set<GrantedAuthority> getAuthorities(Users user) {
        int maxLevel = user.getUserRoleMappings().stream()
                .map(mapping -> mapping.getRole().getLevel())
                .max(Integer::compareTo)
                .orElse(0);

        return user.getUserRoleMappings().stream()
                .map(UserRoleMapping::getRole)
                .filter(role -> role.getLevel() <= maxLevel)
                .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
                .collect(Collectors.toSet());
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
