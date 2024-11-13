package com.app.salty.admin.adminPage.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserRoleUpdateRequest {

    Long userId;
    List<String> roles;
}