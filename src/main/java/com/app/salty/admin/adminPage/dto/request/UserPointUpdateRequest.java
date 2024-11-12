package com.app.salty.admin.adminPage.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPointUpdateRequest {

    private Long userId;  // 포인트를 수정할 유저의 ID
    private Long point;   // 수정할 포인트 값
}