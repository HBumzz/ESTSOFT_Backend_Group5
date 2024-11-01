package com.app.salty.user.repository;


import com.app.salty.user.common.AuthProvider;
import com.app.salty.user.dto.kakao.KakaoUserInfo;
import com.app.salty.user.entity.SocialProvider;

import java.util.Optional;

public interface SocialRepositoryCustom {
    Optional<SocialProvider> findByProviderWithUser(AuthProvider provider, String id);
}
