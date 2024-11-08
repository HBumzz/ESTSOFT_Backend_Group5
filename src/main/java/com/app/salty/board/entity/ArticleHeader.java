package com.app.salty.board.entity;

// 글머리에 들어갈 열거형
public enum ArticleHeader {
    DAILY("일상"),
    SHARE("나눔"),
    TRADE("중고거래");

    final private String name;

    ArticleHeader(String s) {
        this.name= s;
    }
    public String getName() {
        return name;
    }

}
