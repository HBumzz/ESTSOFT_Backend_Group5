package com.app.salty.checklist.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "checklist_item")
public class ChecklistItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private Checklist checklist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false, length = 50)
    private String itemContent;

    @Column(length = 100)
    private String itemMemo;

    @Column(nullable = false, precision = 10, scale = 0)  // 소수점 제거
    private BigDecimal savedAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean isCompleted = false;

}
