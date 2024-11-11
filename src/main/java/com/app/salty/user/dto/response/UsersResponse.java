package com.app.salty.user.dto.response;

import com.app.salty.common.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersResponse {
    private Long userId;
    private String email;
    private String nickname;
    private Long point;
    private String bio;
    private LocalDateTime lastLoginDate;
    private ProfileResponse profile;
    private List<String> levels;

}
