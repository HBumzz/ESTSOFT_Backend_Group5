package com.app.salty.common.entity;

import com.app.salty.user.entity.Users;
import com.app.salty.util.BaseTimeEntity;
import com.app.salty.util.SaltyUtils;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Entity
@Getter
@NoArgsConstructor
public class Profile extends BaseTimeEntity {
    @EmbeddedId
    private ProfileId id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "Profile_original_filename")
    private String originalFilename;
    @Column(name = "Profile_renamed_filename")
    private String renamedFileName;

    @Transient
    private String path;

    //생성 메서드
    @Builder
    public Profile(ProfileId id, Users user, String originalFilename, String renamedFileName) {
        this.id = id;
        this.user = user;
        this.originalFilename = originalFilename;
        this.renamedFileName = renamedFileName;
    }

    //연관관계 메서드
    public void addUser(Users users) {
        this.user = users;
    }

    //business method
    public void profileUpdate(String fileName) {
        this.originalFilename = fileName;
        this.renamedFileName = SaltyUtils.getRenameFilename(fileName);
    }
    public Boolean isDefaultProfile(){
       return this.renamedFileName.equals("default-profile.png");
    }

}
