package com.app.salty.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

import static jakarta.persistence.GenerationType.UUID;

@Slf4j
@Service
public class S3Service {

    private final AmazonS3Client amazonS3Client;
    private final String bucket;

    public S3Service(AmazonS3Client amazonS3Client,
                     @Value("${cloud.aws.s3.bucket}") String bucket) {
        this.amazonS3Client = amazonS3Client;
        this.bucket = bucket;
    }

    public String uploadFile(MultipartFile file) {
        try {
            String fileName = SaltyUtils.getRenameFilename(Objects.requireNonNull(file.getOriginalFilename()));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3Client.putObject(
                    new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata)
            );

            return amazonS3Client.getUrl(bucket, fileName).toString();
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            String fileName = extractFileNameFromUrl(fileUrl);
            amazonS3Client.deleteObject(bucket, fileName);
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", e.getMessage());
            throw new RuntimeException("파일 삭제 실패", e);
        }
    }


    private String extractFileNameFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

}
