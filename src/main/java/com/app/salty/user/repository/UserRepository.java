package com.app.salty.user.repository;

import com.app.salty.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> ,UserRepositoryCustom {

    boolean existsByEmail(String email);

    Optional<Users> findByEmail(String email);

    boolean existsByNickname(String nickname);

}

