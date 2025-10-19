package org.devbid.product.application.awsService;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@EnableConfigurationProperties({AwsProperties.class})
public class S3Config {
    @Bean
    public S3Client s3Client(AwsProperties awsProperties) {
        //AWS SDK v2의 S3Client는 Builder 패턴을 사용
        return S3Client.builder()
                .region(Region.of(awsProperties.getS3().getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                awsProperties.getCredentials().getAccessKey(),
                                awsProperties.getCredentials().getSecretKey()
                        )
                ))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(AwsProperties awsProperties) {
        return S3Presigner.builder()
            .region(Region.of(awsProperties.getS3().getRegion()))
            .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                            awsProperties.getCredentials().getAccessKey(),
                            awsProperties.getCredentials().getSecretKey()
                    )
            ))
            .build();
    }

}
