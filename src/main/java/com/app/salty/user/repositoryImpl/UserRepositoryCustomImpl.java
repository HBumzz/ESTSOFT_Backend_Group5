package com.app.salty.user.repositoryImpl;

import com.app.salty.common.entity.QProfile;
import com.app.salty.user.dto.response.ProfileResponse;
import com.app.salty.user.dto.response.UsersResponse;
import com.app.salty.user.entity.*;
import com.app.salty.user.repository.UserRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Users> findByEmailWithRoles(String email) {
        QUsers user = QUsers.users;
        QUserRoleMapping roleMapping = QUserRoleMapping.userRoleMapping;
        QRoles role = QRoles.roles;

        Users result = queryFactory
                .selectFrom(user)
                .leftJoin(user.userRoleMappings, roleMapping).fetchJoin()
                .leftJoin(roleMapping.role, role).fetchJoin()
                .where(user.email.eq(email))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Users> findByEmailWithProfile(String email) {
        QUsers user = QUsers.users;
        QProfile Profile = QProfile.profile;

        Users result = queryFactory
                .selectFrom(user)
                .leftJoin(user.Profile,Profile).fetchJoin()
                .where(user.email.eq(email))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Users> findByEmailWithAttendances(Long userId) {
        QUsers user = QUsers.users;
        QAttendance attendance = QAttendance.attendance;

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(user)
                        .leftJoin(user.attendances, attendance).fetchJoin()
                        .where(user.id.eq(userId))
                        .fetchOne()
        );
    }
}
