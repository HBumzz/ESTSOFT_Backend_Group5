package com.app.salty.common.entity;

import com.app.salty.user.entity.Users;
import com.app.salty.util.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Entity
@Getter
@NoArgsConstructor
public class Attachment extends BaseTimeEntity {
    @EmbeddedId
    private AttachmentId id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "attachment_original_filename")
    private String originalFilename;
    @Column(name = "attachment_renamed_filename")
    private String renamedFileName;
    @Column(name = "attachment_link")
    private String path;


    //생성 메서드
    @Builder
    public Attachment(AttachmentId id, Users user, String originalFilename, String renamedFileName, String path) {
        this.id = id;
        this.user = user;
        this.originalFilename = originalFilename;
        this.renamedFileName = renamedFileName;
        this.path = path;
    }

}
