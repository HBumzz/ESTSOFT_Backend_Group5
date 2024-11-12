package com.app.salty.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Getter
@Configuration
public class FilePathConfig {

    // 실제 파일이 저장될 외부 경로 (일단 제 기준으로 했습니다 : yoon)
    private final String baseUploadPath = System.getProperty("user.home") + "/Desktop/WorkSpace/Salty/src/main/resources/static/uploads";
//    private final String baseUploadPath = "classpath:/resources/static/uploads/";

    // 각 저장 경로
    private final String userProfilePath = baseUploadPath + "user/";


    //접근 경로
    private final String userProfileUrl = "/uploads/user/";

    //추 후 배포시 경로 맞춰 파일 생성
//    @PostConstruct
//    public void init() {
//        // 필요한 디렉토리 생성
//        createDirectories(
//                userProfilePath
//
//        );
//    }
//
//    private void createDirectories(String... paths) {
//        for (String path : paths) {
//            try {
//                Files.createDirectories(Paths.get(path));
//            } catch (IOException e) {
//                throw new RuntimeException("Failed to create directory: " + path, e);
//            }
//        }
//    }
}
