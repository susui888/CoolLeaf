package org.coollib.leaf.service

import org.coollib.leaf.web.api.UploadUrlResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaTypeFactory
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.util.*

/**
 * Service responsible for handling file upload operations to Cloudflare R2/S3.
 */
@Service
class UploadService(
    private val signer: S3Presigner,
    // Use \${} to escape Kotlin string templates so Spring can evaluate the property placeholder
    @Value($$"${r2.bucket-name}") private val bucketName: String
) {

    /**
     * Generates a list of presigned URLs for uploading files.
     * @param userId The ID of the user performing the upload, used for path isolation.
     * @param fileNames A list of original file names from the client.
     * @return A list of [UploadUrlResponse] containing the presigned URL and the generated object key.
     */
    fun getPresignedUploadUrls(userId: Int, fileNames: List<String>): List<UploadUrlResponse> {
        return fileNames.map { fileName ->
            // Generate a unique object key: users/{userId}/{UUID}-{originalFileName}
            val objectKey = "users/$userId/${UUID.randomUUID()}-$fileName"

            // Automatically detect media type based on file extension, defaulting to image/webp
            val contentType = MediaTypeFactory.getMediaType(fileName)
                .map { it.toString().lowercase() }
                .orElse("image/webp")

            val putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build()

            // Create a presign request with a 10-minute expiration duration
            val presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build()

            // Generate the actual presigned request
            val presignedRequest = signer.presignPutObject(presignRequest)

            // Return the response mapping the presigned URL and the key for later database storage
            UploadUrlResponse(
                uploadUrl = presignedRequest.url().toString(),
                objectKey = objectKey
            )
        }
    }
}
