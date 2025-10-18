package org.devbid.product.application.awsService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    public String upload(MultipartFile mainImage) {
        if (mainImage == null || mainImage.isEmpty()) {
            return null;
        }

        try {
            //파일명 생성
            String fileName = UUID.randomUUID().toString() + extractExtension(mainImage);
            String key = "products/" + fileName;

            //S3에 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(awsProperties.getS3().getBucket())
                    .key(key)
                    .contentType(mainImage.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(
                    mainImage.getInputStream(),
                    mainImage.getSize()
            ));

            //URL 반환
            return buildURL(key);
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 실패 " + mainImage.getOriginalFilename(), e);
        }
    }

    private String buildURL(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
            awsProperties.getS3().getBucket(),
            awsProperties.getS3().getRegion(),
            key
        );
    }

    private String extractExtension(MultipartFile mainImage) {
        String fileName = mainImage.getOriginalFilename();
        return fileName.substring(fileName.lastIndexOf('.'));
    }

    public List<String> uploadMultiple(List<MultipartFile> subImages) {
        return subImages.stream()
                .map(this::upload)
                .collect(Collectors.toList());
    }
}
