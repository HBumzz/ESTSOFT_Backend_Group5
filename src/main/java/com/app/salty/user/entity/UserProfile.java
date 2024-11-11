package com.app.salty.user.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_profile_id")
    private Long id;
    @Column(name = "original_Filename")
    private String originalFilename;
    @Column(name = "renamedFilename")
    private String renamedFileName;

}
