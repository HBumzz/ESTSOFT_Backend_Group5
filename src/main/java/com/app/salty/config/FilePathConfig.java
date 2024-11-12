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

    private final String bucketName ="est-team5-bucket";
    private final String region ="ap-northeast-2";
    // 실제 파일이 저장될 외부 경로 (aws 파일 주소)
    private final String basePath = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
//    private final String baseUploadPath = "classpath:/resources/static/uploads/";

    // 기본이미지 저장
    private final String userDefaultProfilePath = basePath + "user.png";


//    //접근 경로
//    private final String userProfileUrl = "/uploads/user/";

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
