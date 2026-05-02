package org.coollib.leaf.service

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.net.URI

@ExtendWith(MockitoExtension::class)
class UploadServiceTest {

    @Mock
    private lateinit var mockSigner: S3Presigner

    private lateinit var uploadService: UploadService

    private val bucketName = "test-bucket"

    @BeforeEach
    fun setUp() {
        uploadService = UploadService(mockSigner, bucketName)
    }

    @Test
    fun `getPresignedUploadUrls should return valid presigned URLs`() {
        // Given
        val userId = 1
        val fileNames = listOf("image1.jpg", "document.pdf")

        // Mock the PresignedPutObjectRequest that will be returned by the signer
        val mockPresignedRequest = Mockito.mock(PresignedPutObjectRequest::class.java)
        // Stub the url() method of the mocked PresignedPutObjectRequest
        `when`(mockPresignedRequest.url()).thenReturn(URI.create("https://test-bucket.s3.amazonaws.com/mock-url").toURL())

        // Mock the S3Presigner behavior to return our mocked PresignedPutObjectRequest
        `when`(mockSigner.presignPutObject(org.mockito.ArgumentMatchers.any(PutObjectPresignRequest::class.java)))
            .thenReturn(mockPresignedRequest)

        // When
        val responses = uploadService.getPresignedUploadUrls(userId, fileNames)

        // Then
        assertNotNull(responses)
        assertFalse(responses.isEmpty())
        responses.forEach { response ->
            assertNotNull(response.uploadUrl)
            assertNotNull(response.objectKey)
            // Further assertions can be added to validate the format of objectKey and uploadUrl
            // For example, checking if objectKey contains userId and a UUID.
        }
    }
}
