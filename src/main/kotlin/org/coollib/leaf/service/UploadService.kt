package org.coollib.leaf.service

import org.coollib.leaf.web.api.UploadUrlResponse
import org.slf4j.LoggerFactory
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

@Service
class UploadService(
    private val signer: S3Presigner,
    private val s3Client: S3Client,
    @Value("\${r2.bucket-name}") private val bucketName: String
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun getPresignedUploadUrls(userId: Int, fileNames: List<String>): List<UploadUrlResponse> {
        return fileNames.map { fileName ->
            val objectKey = "users/$userId/${UUID.randomUUID()}-$fileName"

            val contentType = MediaTypeFactory.getMediaType(fileName)
                .map { it.toString().lowercase() }
                .orElse("image/webp")

            val putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build()

            val presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build()

            val presignedRequest = signer.presignPutObject(presignRequest)

            UploadUrlResponse(
                uploadUrl = presignedRequest.url().toString(),
                objectKey = objectKey
            )
        }
    }

    fun deleteImages(objectKeys: List<String>) {
        if (objectKeys.isEmpty()) return

        log.info("Executing batch deletion for {} objects in R2 bucket [{}]", objectKeys.size, bucketName)

        val objectIdentifiers = objectKeys.map {
            ObjectIdentifier.builder().key(it).build()
        }

        val deleteRequest = DeleteObjectsRequest.builder()
            .bucket(bucketName)
            .delete { it.objects(objectIdentifiers) }
            .build()

        s3Client.deleteObjects(deleteRequest)
        log.info("Successfully deleted {} objects from R2 bucket", objectKeys.size)
    }
}