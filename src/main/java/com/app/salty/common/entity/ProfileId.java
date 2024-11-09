package com.app.salty.common.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor   //복합키 클래스
public class ProfileId implements Serializable {
    private ProfileType type;
    private Long userId;
}
