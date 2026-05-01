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

@Service
class UploadService(
    private val signer: S3Presigner,
    @Value($$"${r2.bucket-name}") private val bucketName: String
) {

    fun getPresignedUploadUrls(userId: Int, fileNames: List<String>): List<UploadUrlResponse> {
        return fileNames.map { fileName ->
            val objectKey = "users/$userId/${UUID.randomUUID()}-$fileName"

            val contentType = MediaTypeFactory.getMediaType(fileName)
                .map { it.toString().lowercase() }
                .orElse("image/jpeg")

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
}

