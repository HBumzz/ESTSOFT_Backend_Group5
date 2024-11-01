package com.app.salty.user.dto.kakao;

import com.app.salty.user.common.AuthProvider;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfo {
    private String id;
    private AuthProvider provider;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    public String getEmail() {
        return kakaoAccount.getEmail();
    }

    public String getNickname() {
        return kakaoAccount.getProfile().getNickname();
    }

    public String getProfileImage() {
        return kakaoAccount.getProfile().getProfileImageUrl();
    }

    public void setAuthProvider(AuthProvider provider) {
        this.provider = provider;
    }


    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {
        private String email;
        private Profile profile;
    }

    @Getter
    @NoArgsConstructor
    public static class Profile {
        private String nickname;

        @JsonProperty("profile_image_url")
        private String profileImageUrl;
    }
}