package com.app.salty.user.service;


import com.app.salty.config.globalExeption.custom.DuplicateEmailException;
import com.app.salty.user.common.AuthProvider;
import com.app.salty.user.common.Role;
import com.app.salty.user.common.social.KakaoAPI;
import com.app.salty.user.dto.kakao.KakaoUserInfo;
import com.app.salty.user.dto.request.UserSignupRequest;
import com.app.salty.user.dto.kakao.KAKAOAuthResponse;
import com.app.salty.user.dto.response.UserResponse;
import com.app.salty.user.entity.Roles;
import com.app.salty.user.entity.SocialProvider;
import com.app.salty.user.entity.UserRoleMapping;
import com.app.salty.user.entity.Users;
import com.app.salty.user.repository.RolesRepository;
import com.app.salty.user.repository.SocialRepository;
import com.app.salty.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final SocialRepository socialRepository;
    private final PasswordEncoder passwordEncoder;
    private final KakaoAPI kakaoAPI;

    @Transactional
    public UserResponse signup(UserSignupRequest request) {
        validateDuplicateEmail(request.getEmail());


        Users signupUser = userSignupRequestToUser(request);
        Roles userRole = rolesRepository.findByRole(Role.USER)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 권한입니다."));

        addUserRole(signupUser,Role.USER);

        Users savedUser = userRepository.save(signupUser);
        log.info("Saved user: {}", savedUser);

        return convertUserResponse(savedUser);
    }


    //이메일 중복 검사
    private void validateDuplicateEmail(String email) {
        if(userRepository.existsByEmail(email))
            throw new DuplicateEmailException("이미 가입된 이메일입니다");
    }

    public boolean verifyNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    //기본 권환 추가
    private void addUserRole(Users user, Role role) {
        Roles userRole = rolesRepository.findByRole(role)
                .orElseThrow(() -> new RuntimeException("기본 역할이 없습니다."));
        UserRoleMapping userRoleMapping = UserRoleMapping.builder()
                .user(user)
                .role(userRole)
                .build();
        user.addRoleMappings(userRoleMapping);
    }

    //빌드시
    @PostConstruct
    public void initRoles() {
        if (rolesRepository.count() == 0) {
            List<Roles> roles = Arrays.asList(
                    new Roles(Role.USER,"기본 사용자 권한",1),
                    new Roles(Role.USER2,"레벨2 사용자 권한",2),
                    new Roles(Role.USER3,"레벨3 사용자 권한",3),
                    new Roles(Role.USER4,"레벨5 사용자 권한",4),
                    new Roles(Role.ADMIN,"관리자 권한",5)
            );
            rolesRepository.saveAll(roles);
        }
    }

    //카카오 로그인
    @Transactional
    public Users kakaoLogin(String code) {
        String accessToken = getKakaoAccessToken(code);
        log.info("Access token: {}", accessToken);
        KakaoUserInfo userInfo = getKakaoUserInfo(accessToken);
        log.info("Kakao user info: {}", userInfo);
        userInfo.setAuthProvider(kakaoAPI.getAuthProvider());

        return processKakaoUser(userInfo);
    }

    //인증 코드로 액세스 토큰 요청
    private String getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoAPI.getClientId());
        params.add("redirect_uri", kakaoAPI.getRedirectUri());
        params.add("code", code);

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KAKAOAuthResponse> response = restTemplate.postForEntity(
                kakaoAPI.getKAKAO_TOKEN_URL(),
                request,
                KAKAOAuthResponse.class
        );

        if (response.getBody() != null) {
            return response.getBody().getAccessToken();
        }

        throw new IllegalArgumentException("kakao token 인증 실패");
    }

    //사용자 정보 요청
    private KakaoUserInfo getKakaoUserInfo(String accessToken) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
                //setBearerAuth(accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                //setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                kakaoAPI.getKAKAO_USER_INFO_URL(),
                HttpMethod.GET,
                request,
                KakaoUserInfo.class
        );

        if (response.getBody() != null) {
            return response.getBody();
        }

        throw new IllegalArgumentException("카카오 사용자 정보를 가져오는데 실패했습니다.");
    }


    private Users processKakaoUser(KakaoUserInfo userInfo) {
        return socialRepository.findByProviderWithUser(AuthProvider.KAKAO, userInfo.getId())
                .map(SocialProvider::getUser)
                .orElseGet(()->createKaKaoUser(userInfo));

    }


    //유저 저장
    private Users createKaKaoUser(KakaoUserInfo userInfo) {
        if(userInfo.getId() ==null)
            throw new OAuth2AuthenticationException("카카오 client ID 없음");

        Users user = socialProviderSignToUser(userInfo);
        SocialProvider socialProvider = createSocialProvider(user,userInfo);
        user.addSocialProvider(socialProvider);
        addUserRole(user,Role.USER);

        return userRepository.save(user);
    }


    //convert
    private SocialProvider createSocialProvider(Users user, KakaoUserInfo userInfo) {
        return SocialProvider.builder()
                .provider(userInfo.getProvider())
                .providerId(userInfo.getId())
                .nickname(user.getNickname())
                .build();
    }

    private Users socialProviderSignToUser(KakaoUserInfo userInfo) {
        String email = userInfo.getEmail() != null ? userInfo.getEmail() : userInfo.getId();
        String nickname = userInfo.getNickname() != null ? userInfo.getNickname() : userInfo.getId();

        return Users.builder()
                .email(email)
                .nickname(nickname)
                .activated(true)
                .password(passwordEncoder.encode(userInfo.getId()))
                .build();
    }

    private Users userSignupRequestToUser(UserSignupRequest request) {
        return Users.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .activated(true)
                .build();

    }

    private UserResponse convertUserResponse(Users user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
