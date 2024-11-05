package com.app.salty.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentResponse {
    private String type;
    private String originalFilename;
    private String path;
    private Long id;

    public String getPath() {
        return path != null ? path : "/images/default-profile.png";
    }
}
