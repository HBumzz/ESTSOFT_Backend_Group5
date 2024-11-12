package com.app.salty.checklist.entity;

import lombok.Getter;

@Getter
public enum CategoryType {
    FOOD("식비"),
    TRANSPORT("교통비"),
    SNACK("간식비"),
    ETC("기타");

    private final String description;

    CategoryType(String description) {
        this.description = description;
    }

}

