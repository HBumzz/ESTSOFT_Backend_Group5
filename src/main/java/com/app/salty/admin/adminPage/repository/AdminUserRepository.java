package com.app.salty.admin.adminPage.repository;

import com.app.salty.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByNickname(String nickname);
    Optional<Users> findByEmail(String email);
    List<Users> findByEmailLikeOrNicknameLikeOrDescriptionLike(String query, String query1, String query2);
}