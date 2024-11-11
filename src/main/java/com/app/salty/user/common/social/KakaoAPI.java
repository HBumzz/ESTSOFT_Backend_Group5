package com.app.salty.user.common.social;

import com.app.salty.user.common.AuthProvider;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KakaoAPI {

    @Value("${kakao.client_id}")
    private String clientId;
    @Value("${kakao.secret_key}")
    private String secretKet;
    @Value("${kakao.redirect_uri}")
    private String redirectUri;
    @Value("${kakao.admin_key}")
    private String adminKey;

    private final AuthProvider authProvider = AuthProvider.KAKAO;
    private final String KAUTH_TOKEN_URL_HOST ="https://kauth.kakao.com";
    private final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
    private final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";


}
