package com.app.salty.user.repository;

import com.app.salty.user.common.Role;
import com.app.salty.user.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByRole(Role role);
}
