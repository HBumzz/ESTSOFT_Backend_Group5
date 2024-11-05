package com.app.salty.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Images {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="image_id")
    private Long id;

    @Column(name="originalFileName", nullable = false)
    private String originalFileName;

    @Column(name="storedFileName", nullable = false)
    private String storedFileName;

    @Column(name="filePath", nullable = false)
    private String filePath;

    @Column(name="fileSize")
    private Long fileSize;

    @Column(name="fileExtension")
    private String fileExtension;

    @ManyToOne
    @JoinColumn(name="article_id")
    private Article article;

    public Images(String originalFileName, String originalFileName1
            , String filePath, long size
            , String contentType, Article article) {
        this.originalFileName=originalFileName;
        this.storedFileName = originalFileName1;
        this.filePath = filePath;
        this.fileSize= size;
        this.fileExtension = contentType;
        this.article = article;
    }
}
