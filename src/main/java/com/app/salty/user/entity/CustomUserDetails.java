package com.app.salty.user.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@ToString
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String nickname;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;


    @Builder
    public CustomUserDetails(Long id, String username, String nickname, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of();
//    }

    @Override
    public String getUsername() {
        return username;
    }
}
