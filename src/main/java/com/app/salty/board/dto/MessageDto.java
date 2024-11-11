package com.app.salty.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class MessageDto {
    private String message="";              // 사용자에게 전달할 메시지
    private String redirectUri="";          // 리다이렉트 URI

    public MessageDto(String s, String uri) {
        this.message=s;
        this.redirectUri=uri;
    }
}
