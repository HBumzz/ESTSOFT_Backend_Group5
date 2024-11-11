package com.app.salty.board.entity;

public enum ArticleType {
    BBS("게시판");

    final private String name;

    ArticleType(String s) {
        this.name= s;
    }
    public String getName() {
        return name;
    }

}
