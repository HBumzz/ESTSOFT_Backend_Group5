package com.app.salty.user.repository;

import com.app.salty.user.entity.Users;

import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<Users> findByEmailWithRoles(String email);
    Optional<Users> findByEmailWithProfile(String email);
    Optional<Users> findByEmailWithAttendances(Long userId);
}
