package org.coollib.leaf.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

/**
 * Configuration class for R2 (Cloudflare R2) object storage.
 * This class sets up S3Client and S3Presigner beans for interacting with R2.
 */
@Configuration
class R2Config {

    // Injects the R2 access key from application properties.
    @Value($$"${r2.access-key}")
    private lateinit var accessKey: String

    // Injects the R2 secret key from application properties.
    @Value($$"${r2.secret-key}")
    private lateinit var secretKey: String

    // Injects the R2 endpoint URL from application properties.
    @Value($$"${r2.endpoint}")
    private lateinit var endpoint: String

    // Injects the R2 bucket name from application properties.
    @Value($$"${r2.bucket-name}")
    private lateinit var bucketName: String

    /**
     * Configures and provides an S3Presigner bean for generating pre-signed URLs for R2.
     * This presigner is essential for securely allowing temporary access to R2 objects.
     * @return An initialized S3Presigner instance.
     */
    @Bean
    fun s3Presigner(): S3Presigner {
        // Create AWS basic credentials using the injected access and secret keys.
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)

        // Key: Configure S3 service behavior for R2 compatibility.
        val s3Configuration = S3Configuration.builder()
            .pathStyleAccessEnabled(true) // R2 requires path-style access to be enabled.
            .checksumValidationEnabled(false) // Disable checksum validation for R2.
            .build()

        return S3Presigner.builder()
            // Override the default endpoint with the R2 endpoint.
            .endpointOverride(URI.create(endpoint))
            // Provide static credentials for authentication.
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            // Set the region to "auto" as recommended for Cloudflare R2.
            .region(Region.of("auto"))
            // Apply the R2-specific service configuration.
            .serviceConfiguration(s3Configuration)
            .build()
    }
}