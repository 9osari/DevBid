package org.devbid.product.application.awsService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class S3Service {
    private final AwsProperties awsProperties;
    private final S3Presigner s3Presigner;

    public PresignedUrlData generatePresignedUrl(String fileName, String contentType) {
        nameAndTypeValidation(fileName, contentType);
        String key = makeKey(fileName);

        //PutObjectRequest 생성
        PutObjectRequest putObjectRequest = getPutObjectRequest(contentType, key);

        //PutObjectPresignRequest 생성
        PutObjectPresignRequest presignRequest = getPutObjectPresignRequest(putObjectRequest);

        //pre-signed URL 생성
        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(presignRequest);

        String uploadUrl = presignedPutObjectRequest.url().toString();

        //URL과 key 반환
        return new PresignedUrlData(uploadUrl, key);

    }

    private static void nameAndTypeValidation(String fileName, String contentType) {
        if(fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("fileName is null or empty");
        }
        if(contentType == null || contentType.isEmpty()) {
            throw new IllegalArgumentException("contentType is null or empty");
        }
    }

    private static String makeKey(String fileName) {
        //key 생성
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        return "products/" + UUID.randomUUID().toString() + extension;
    }

    private PutObjectRequest getPutObjectRequest(String contentType, String key) {
        return PutObjectRequest.builder()
                .bucket(awsProperties.getS3().getBucket())
                .key(key)
                .contentType(contentType)
                .build();
    }

    private PutObjectPresignRequest getPutObjectPresignRequest(PutObjectRequest putObjectRequest) {
        return PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest).build();
    }

    public String buildPublicUrl(String key) {
        return String.format("https://%s/%s",
                awsProperties.getCloudfront().getDomain(),
                key
        );
    }

    public String generatePresignedGetUrl(String key) {
        GetObjectRequest getObjectRequest = getGetObjectRequest(key);
        //GetObjectPresignRequest 생성
        GetObjectPresignRequest presignRequest = getGetObjectPresignRequest(getObjectRequest);
        //pre-signed GET URL 생성
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    private GetObjectRequest getGetObjectRequest(String key) {
        //GetObjectRequest 생성
        return GetObjectRequest.builder()
                .bucket(awsProperties.getS3().getBucket())
                .key(key)
                .build();
    }

    private static GetObjectPresignRequest getGetObjectPresignRequest(GetObjectRequest getObjectRequest) {
        return GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1))
                .getObjectRequest(getObjectRequest)
                .build();
    }
}