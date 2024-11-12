package com.app.salty.checklist.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name="checklist")
public class Checklist {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checklistId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChecklistType typeName;

    @Column(nullable = false)
    private Integer completionRate = 0;

    @Column(nullable = false)
    private LocalDateTime checklistCreatedAt;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL)
    private List<ChecklistItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.checklistCreatedAt = LocalDateTime.now();
    }
}
