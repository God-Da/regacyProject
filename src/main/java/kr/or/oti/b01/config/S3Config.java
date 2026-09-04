package kr.or.oti.b01.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {

    @Bean
    public AmazonS3 amazonS3() {
        // AWS 자격 증명 체크 없이 로컬에서도 즉시 기동되도록 익명 인증 + 서울 리전 고정
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();
    }
}