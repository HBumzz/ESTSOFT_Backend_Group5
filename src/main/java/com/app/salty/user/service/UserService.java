package com.app.salty.user.service;


import com.app.salty.common.entity.Profile;
import com.app.salty.common.entity.ProfileId;
import com.app.salty.common.entity.ProfileType;
import com.app.salty.config.FilePathConfig;
import com.app.salty.config.globalExeption.custom.AttendanceException;
import com.app.salty.config.globalExeption.custom.DuplicateEmailException;
import com.app.salty.user.common.AuthProvider;
import com.app.salty.user.common.Role;
import com.app.salty.user.common.social.KakaoAPI;
import com.app.salty.user.dto.kakao.KakaoUserInfo;
import com.app.salty.user.dto.request.UserUpdateRequest;
import com.app.salty.user.dto.request.UserSignupRequest;
import com.app.salty.user.dto.kakao.KAKAOAuthResponse;
import com.app.salty.user.dto.response.*;
import com.app.salty.user.entity.*;
import com.app.salty.user.repository.RolesRepository;
import com.app.salty.user.repository.SocialRepository;
import com.app.salty.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
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
    private final FilePathConfig filePathConfig;

    public Users findBy(Long userId) {
        return userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
    }

    @Transactional
    public UserResponse signup(UserSignupRequest request) {
        validateDuplicateEmail(request.getEmail());

        Users signupUser = userSignupRequestToUser(request);
        addUserRole(signupUser,Role.USER);

        Users savedUser = userRepository.save(signupUser);
        log.info("Saved user: {}", savedUser);

        return convertUserResponse(savedUser);
    }

    //유저 정보 갖고오기 및 프로필 없을 시 생성
    @Transactional
    public UsersResponse findByUserWithProfile(CustomUserDetails currentUser) {
        Users user = userRepository.findByEmailWithProfile(currentUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("UserService.findByUserWithProfile::::::User not found with email: " + currentUser.getUsername()));
        user.updateLastActivityDate();
        log.info("Found user: {}", user);
        if(user.getProfile() == null) {
            System.out.println("프로필 없음 메서드 실행 ");
            Profile Profile = createDefaultProfile(user);
            user.addProfile(Profile);
        }
        return userToUsersResponse(user,currentUser.getAuthorities());
    }

    //프로필 수정 -닉네임 및 소개글
    @Transactional
    public UsersResponse updateProfile(CustomUserDetails currentUser, UserUpdateRequest userUpdateRequest) {
        Users user = userRepository.findByEmailWithProfile(currentUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("UserService.findByUserWithProfile::::::User not found with email: " + currentUser.getUsername()));

        user.updateDescription(userUpdateRequest.getBio());
        user.updateNickname(userUpdateRequest.getNickname());

        return userToUsersResponse(user, currentUser.getAuthorities());
    }

    //프로필 이미지 업데이트
    @Transactional
    public String updateProfileImage(String email, MultipartFile file) throws IOException {
        Users user = userRepository.findByEmailWithProfile(email)
                .orElseThrow(() -> new IllegalArgumentException("UserService.findByUserWithProfile::::::User not found with email: " + email));

        Profile profile = user.getProfile();
        //기본 이미지 아닐 시 파일 삭제
        deleteExistingProfile(profile);

        //파일 업데이트
        profile.profileUpdate(file.getOriginalFilename());

        return saveProfileImage(file,profile);
    }

    //파일 경로 생성 및 파일 추가 -경로 리턴
    private String saveProfileImage(MultipartFile file, Profile profile) throws IOException {
        Path targetPath = Paths.get(filePathConfig.getUserProfilePath(), profile.getRenamedFileName());
        // 디렉토리 생성 확인
        Files.createDirectories(targetPath.getParent());
        // 파일 저장
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return filePathConfig.getUserProfilePath()+profile.getRenamedFileName();
    }

    //기존 파일 데이터 삭제
    private void deleteExistingProfile(Profile profile) throws IOException {
        if(profile != null && !profile.isDefaultProfile()) {
            Path existingFilePath = Paths.get(filePathConfig.getUserProfilePath(),
                    profile.getRenamedFileName());

            if (Files.deleteIfExists(existingFilePath)) {
                log.info("Successfully deleted existing profile image: {}", existingFilePath);
            }
        }
    }

    //출석체크 리스트
    public AttendanceResponse findByUserWithAttendance(Long userId) {
        Users user = userRepository.findByEmailWithAttendances(userId)
                .orElseThrow();
        List<Attendance> thisMonthAttendanceList = thisMonthAttendance(user.getAttendances());

        return createAttendanceResponse(user,thisMonthAttendanceList);
    }

    //오늘 출석체크
    @Transactional
    public AttendanceResponse updateUserAttendance(Long userId) {
        Users user = userRepository.findByEmailWithAttendances(userId)
                .orElseThrow();
       todayAttendance(user);

       List<Attendance> thisMonthAttendanceList = thisMonthAttendance(user.getAttendances());

       return createAttendanceResponse(user,thisMonthAttendanceList);
    }

    //출석 중복 확인
    private void todayAttendance(Users user) {
        if(user.getAttendances().stream()
                .anyMatch(attendance -> attendance.getAttendanceDate().equals(LocalDate.now()))){
            throw new AttendanceException("이미 출석체크를 하셨습니다.");
        }
        Attendance attendance = createAttendance();
        user.addPoint(attendance.getRewardPoint());
        user.addAttendance(attendance);
    }

    //이번달 출석일수 및 출석률
    public List<Attendance> thisMonthAttendance(List<Attendance> attendanceList) {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        return attendanceList.stream()
                .filter(a -> !a.getAttendanceDate().isBefore(firstDayOfMonth)
                && !a.getAttendanceDate().isAfter(lastDayOfMonth))
                .toList();
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
                .lastActivityDate(LocalDateTime.now())
                .point(0L)
                .build();
    }

    private Users userSignupRequestToUser(UserSignupRequest request) {
        return Users.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .activated(true)
                .point(0L)
                .lastActivityDate(LocalDateTime.now())
                .build();

    }

    private UserResponse convertUserResponse(Users user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .point(user.getPoint())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())

                .build();
    }


    private UsersResponse userToUsersResponse(Users user, Collection<? extends GrantedAuthority> authorities) {
        return UsersResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profile(profileToResponse(user.getProfile()))
                .bio(user.getDescription())
                .point(user.getPoint())
                .lastLoginDate(user.getLastActivityDate())
                .levels(
                        authorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList()
                )
                .build();
    }

    private ProfileResponse profileToResponse(Profile profile) {
        return ProfileResponse.builder()
                .type(ProfileType.PROFILE.toString())
                .path(filePathConfig.getUserProfileUrl()+profile.getRenamedFileName())
                .originalFilename(profile.getOriginalFilename())
                .renamedFilename(profile.getRenamedFileName())
                .id(profile.getId().getUserId())
                .build();
    }

    private Profile createDefaultProfile(Users user) {
        return Profile.builder()
                .id(new ProfileId(ProfileType.PROFILE,user.getId()))
                .originalFilename("default-profile.png")
                .renamedFileName("default-profile.png")
                .user(user)
                .build();
    }

    private AttendanceResponse createAttendanceResponse(Users user, List<Attendance> thisMonthAttendanceList) {
        return AttendanceResponse.builder()
                .attendanceList(user.getAttendances().stream().map(this::createAttendanceDTO).toList())
                .totalDays(user.getAttendances().size())
                .consecutiveDays(thisMonthAttendanceList.size())
                .monthlyRate((int)((double) thisMonthAttendanceList.size() / LocalDate.now().lengthOfMonth() * 100))
                .point(user.getPoint())
                .build();
    }

    private Attendance createAttendance() {
        return Attendance.builder()
                .attendanceDate(LocalDate.now())
                .build();
    }

    private AttendanceDTO createAttendanceDTO(Attendance attendance) {
        return AttendanceDTO.builder()
                .attendanceDate(attendance.getAttendanceDate())
                .attendanceId(attendance.getId())
                .build();
    }

}
