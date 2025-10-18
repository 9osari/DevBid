package org.devbid.product.application.awsService;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "cloud.aws")
public class AwsProperties {
    private final S3 s3;
    private final Credentials credentials;
    private final Region region;

    public AwsProperties(S3 s3, Credentials credentials, Region region) {
        this.s3 = s3;
        this.credentials = credentials;
        this.region = region;
    }

    @Getter
    public static class S3 {
        private final String bucket;
        private final String region;

        public S3(String bucket, String region) {
            this.bucket = bucket;
            this.region = region;
        }
    }

    @Getter
    public static class Credentials {
        private final String accessKey;
        private final String secretKey;

        public Credentials(String accessKey, String secretKey) {
            this.accessKey = accessKey;
            this.secretKey = secretKey;
        }
    }

    @Getter
    public static class Region {
        private final String staticRegion;

        public Region(String staticRegion) {
            this.staticRegion = staticRegion;
        }
    }
}

