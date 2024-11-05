package com.app.salty.board.dto.ImagesDto;

import com.app.salty.board.entity.Images;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ImagesResponseDto {
    private Long imageId;
    private String originalFileName;
    private String storedFileName;
    private String filePath;

    public ImagesResponseDto(Images image) {
        this.imageId = image.getId();
        this.originalFileName = image.getOriginalFileName();
        this.storedFileName = image.getStoredFileName();
        this.filePath =image.getFilePath();
    }
}
