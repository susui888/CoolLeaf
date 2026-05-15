package org.coollib.leaf.service

import org.coollib.leaf.web.api.UploadUrlResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaTypeFactory
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest
import software.amazon.awssdk.services.s3.model.ObjectIdentifier
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.util.*

/**
 * Service responsible for handling file operations (Upload & Delete) with Cloudflare R2 / S3-compatible storage.
 *
 * It manages presigned URL generation for secure client-side uploads and
 * provides server-side utilities for resource cleanup.
 */
@Service
class UploadService(
    private val signer: S3Presigner,
    private val s3Client: S3Client,
    // Use \${} to escape Kotlin string templates so Spring can evaluate the property placeholder
    @Value($$"${r2.bucket-name}") private val bucketName: String
) {

    /**
    * Generates a list of presigned URLs for client-side file uploads.
    *
    * This allows the mobile/frontend client to upload images directly to R2
    * without passing through the Spring Boot backend, reducing server load.
    *
    * @param userId The ID of the user performing the upload, used for directory partitioning.
    * @param fileNames A list of original file names provided by the client.
    * @return A list of [UploadUrlResponse] containing the temporary upload URL and the persistent object key.
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

    /**
    * Performs a batch deletion of objects from the storage bucket.
    *
    * This is typically used during review deletion or when images are replaced
    * to prevent "orphaned" files from occupying storage space.
    *
    * @param objectKeys A list of unique object keys (paths) to be removed.
    */
    fun deleteImages(objectKeys: List<String>) {
        if (objectKeys.isEmpty()) return

        // Map string keys to S3 ObjectIdentifiers
        val objectIdentifiers = objectKeys.map {
            ObjectIdentifier.builder().key(it).build()
        }

        // Prepare the batch delete request for efficiency
        val deleteRequest = DeleteObjectsRequest.builder()
            .bucket(bucketName)
            .delete { it.objects(objectIdentifiers) }
            .build()

        // Execute the deletion via the S3 Client
        s3Client.deleteObjects(deleteRequest)
    }
}
