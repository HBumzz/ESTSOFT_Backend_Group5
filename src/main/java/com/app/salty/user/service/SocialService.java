package com.app.salty.user.service;

import com.app.salty.user.common.AuthProvider;
import com.app.salty.user.common.social.KakaoAPI;
import com.app.salty.user.dto.kakao.KAKAOAuthResponse;
import com.app.salty.user.dto.kakao.KakaoUserInfo;
import com.app.salty.user.entity.SocialProvider;
import com.app.salty.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SocialService {

//    private final KakaoAPI kakaoAPI;
//
//    public KAKAOAuthResponse kakaoLogin(String code) {
//        String accessToken = getKakaoAccessToken(code);
//
//        KakaoUserInfo userInfo = getKakaoUserInfo(accessToken);
//
//        SocialProvider socialUser = checkUser(userInfo);
//
//        return null;
//    }
//
//    //인증 코드로 액세스 토큰 요청
//    private String getKakaoAccessToken(String code) {
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "authorization_code");
//        params.add("client_id", kakaoAPI.getClientId());
//        params.add("redirect_uri", kakaoAPI.getRedirectUri());
//        params.add("code", code);
//
//        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(params, headers);
//
//        ResponseEntity<KAKAOAuthResponse> response = restTemplate.postForEntity(
//                "https://kauth.kakao.com/oauth/token",
//                request,
//                KAKAOAuthResponse.class
//        );
//
//        if (response.getBody() != null) {
//            return response.getBody().getAccessToken();
//        }
//
//        throw new IllegalArgumentException("kakao token 인증 실패");
//    }
//
//    //사용자 정보 요청
//    private KakaoUserInfo getKakaoUserInfo(String accessToken) {
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(accessToken);
//
//        HttpEntity<String> request = new HttpEntity<>(headers);
//
//        ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
//                "https://kapi.kakao.com/v2/user/me",
//                HttpMethod.GET,
//                request,
//                KakaoUserInfo.class
//        );
//
//        if (response.getBody() != null) {
//            return response.getBody();
//        }
//
//        throw new IllegalArgumentException("카카오 사용자 정보를 가져오는데 실패했습니다.");
//    }
//
//
//    private SocialProvider checkUser(KakaoUserInfo userInfo) {
//        SocialProvider existingSocialProvider = socialProviderRepository
//                .findByProviderAndProviderId(AuthProvider.KAKAO, userInfo.getId());
//
//        return userRepository.save(user);
//
//    }
//
//    private Users createUser(KakaoUserInfo userInfo) {
//
//    }
//
//    private Users updateUser(Users entity, KakaoUserInfo userInfo) {
//
//    }
}
