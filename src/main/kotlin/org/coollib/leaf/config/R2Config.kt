package org.coollib.leaf.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

@Configuration
class R2Config {

    @Value($$"${r2.access-key}")
    private lateinit var accessKey: String

    @Value($$"${r2.secret-key}")
    private lateinit var secretKey: String

    @Value($$"${r2.endpoint}")
    private lateinit var endpoint: String

    @Value($$"${r2.bucket-name}")
    private lateinit var bucketName: String

    @Bean
    fun s3Client(): S3Client {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)

        return S3Client.builder()
            .endpointOverride(URI.create(endpoint))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.of("auto"))
            .forcePathStyle(true)
            .build()
    }

    @Bean
    fun s3Presigner(): S3Presigner {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)

        // 关键：配置 S3 服务的行为
        val s3Configuration = S3Configuration.builder()
            .pathStyleAccessEnabled(true) // R2 必须开启路径风格访问
            .checksumValidationEnabled(false) // 禁用校验和
            .build()

        return S3Presigner.builder()
            .endpointOverride(URI.create(endpoint))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.of("auto"))
            .serviceConfiguration(s3Configuration) // 应用配置
            .build()
    }
}