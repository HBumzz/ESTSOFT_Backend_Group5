package com.app.salty.admin.adminPage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPointUpdateResponse {

    private Long userId;    // 유저 ID
    private Long updatedPoint;  // 수정된 포인트 값
    private String message;  // 처리 메시지
}