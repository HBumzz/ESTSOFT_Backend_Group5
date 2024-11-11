package com.app.salty.user.repositoryImpl;

import com.app.salty.user.common.AuthProvider;
import com.app.salty.user.entity.QSocialProvider;
import com.app.salty.user.entity.QUserRoleMapping;
import com.app.salty.user.entity.QUsers;
import com.app.salty.user.entity.SocialProvider;
import com.app.salty.user.repository.SocialRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class SocialRepositoryCustomImpl implements SocialRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<SocialProvider> findByProviderWithUser(AuthProvider provider, String id) {

        QSocialProvider socailProvider = QSocialProvider.socialProvider;
        QUsers user = QUsers.users;
        QUserRoleMapping userRoleMapping = QUserRoleMapping.userRoleMapping;

        SocialProvider result = queryFactory
                .selectFrom(socailProvider)
                .leftJoin(socailProvider.user,user).fetchJoin()
                .leftJoin(user.userRoleMappings,userRoleMapping).fetchJoin()
                .where(
                        socailProvider.provider.eq(provider),
                        socailProvider.providerId.eq(id)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

}
