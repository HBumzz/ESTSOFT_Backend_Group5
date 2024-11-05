package com.app.salty.board.entity;

import com.app.salty.board.dto.article.UpdateArticleRequestDto;
import com.app.salty.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="article_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(name ="article_header", nullable = false)
    private ArticleHeader header;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @CreatedDate
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
    /*
    @OneToMany(mappedBy = "article")
    private List<Images> uploadImages = new ArrayList<>();
*/

    public Article(Users user, ArticleHeader header, String title, String content) {
        this.user = user;
        this.header=header;
        this.title=title;
        this.content=content;
    }

    public Article(Long id, Users user, ArticleHeader header, String title, String content) {
        this.id = id;
        this.user = user;
        this.header=header;
        this.title=title;
        this.content=content;
    }
}
