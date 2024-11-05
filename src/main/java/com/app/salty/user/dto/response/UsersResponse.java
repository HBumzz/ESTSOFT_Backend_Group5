package com.app.salty.user.dto.response;

import com.app.salty.common.entity.Attachment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersResponse {
    private Long userId;
    private String email;
    private String nickname;
    private AttachmentResponse profile;

}
