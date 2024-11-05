package com.app.salty.board.entity;

// 글머리에 들어갈 열거형
public enum ArticleHeader {
    DAILY("일상"),          // 일상
    CERTIFY("인증"),        // 절약/소비 인증
    INFORMATION("정보"),    // 정보
    WORRY("고민"),          // 고민
    QUESTION("질문/답변");        // 질문/답변

    final private String name;

    ArticleHeader(String s) {
        this.name= s;
    }
    public String getName() {
        return name;
    }

}
