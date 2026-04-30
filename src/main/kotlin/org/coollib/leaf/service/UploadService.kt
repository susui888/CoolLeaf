package org.coollib.leaf.service

import org.coollib.leaf.web.api.UploadUrlResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.util.UUID

@Service
class UploadService(
    private val signer: S3Presigner,
    @Value($$"${r2.bucket-name}") private val bucketName: String
) {

    fun getPresignedUploadUrls(userId: Int, fileNames: List<String>): List<UploadUrlResponse> {
        return fileNames.map { fileName ->
            val objectKey = "users/$userId/${UUID.randomUUID()}-$fileName"

            val putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType("image/webp")
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
}

