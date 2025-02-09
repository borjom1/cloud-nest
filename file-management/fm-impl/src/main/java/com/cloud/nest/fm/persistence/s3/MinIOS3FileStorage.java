package com.cloud.nest.fm.persistence.s3;

import com.cloud.nest.fm.config.MinIOProperties;
import com.cloud.nest.fm.model.SingleFileUpload;
import com.cloud.nest.platform.infrastructure.streaming.ContentRangeSelection;
import com.cloud.nest.platform.model.exception.UnexpectedException;
import io.minio.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Component
public class MinIOS3FileStorage implements S3FileStorage {

    private static final long DEFAULT_PART_SIZE = 5 * 1024 * 1024; // 5 MB

    private final String bucketName;
    private final int partSize;
    private final S3FileNaming objectKeyCreator;
    private final MinioClient minioClient;

    public MinIOS3FileStorage(
            MinIOProperties minIOProperties,
            S3FileNaming objectKeyCreator,
            MinioClient minioClient
    ) {
        this.objectKeyCreator = objectKeyCreator;
        this.minioClient = minioClient;
        this.bucketName = minIOProperties.getBuckets().getDefaultBucket();
        this.partSize = minIOProperties.getBuckets().getPartSize();
    }

    @NotBlank
    @Override
    public String uploadFile(
            @NotNull Long userId,
            @NotNull MultipartFile file,
            @NotNull Map<String, String> metadata
    ) {
        ensureBucketExists();
        final String s3ObjectKey = objectKeyCreator.getObjectKey(userId);

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(s3ObjectKey)
                            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                            .userMetadata(metadata)
                            .stream(file.getInputStream(), file.getSize(), partSize)
                            .build()
            );
            return s3ObjectKey;
        } catch (Exception e) {
            throw new UnexpectedException(
                    "Cannot upload file [%s.%s] to bucket [%s]".formatted(
                            metadata.get(S3FileStorage.FILENAME_META),
                            metadata.get(S3FileStorage.FILE_EXT_META),
                            bucketName
                    ),
                    e
            );
        }
    }

    @NotBlank
    public String uploadFile(
            @NotNull Long userId,
            @NotNull SingleFileUpload fileUpload,
            @NotNull Map<String, String> metadata
    ) {
        ensureBucketExists();
        final String s3ObjectKey = objectKeyCreator.getObjectKey(userId);
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(s3ObjectKey)
                            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                            .userMetadata(metadata)
                            .stream(fileUpload.getFileInput().getInputStream(), fileUpload.getSize(), DEFAULT_PART_SIZE)
                            .build()
            );
        } catch (IOException e) {
            if (e.getMessage().contains("unexpected EOF")) {
                throw new IllegalArgumentException("Failed to upload file [%s.%s] due to invalid file size".formatted(
                        metadata.get(S3FileStorage.FILENAME_META),
                        metadata.get(S3FileStorage.FILE_EXT_META)
                ));
            } else {
                throwUnexpectedUploadError(metadata, e);
            }
        } catch (Exception e) {
            throwUnexpectedUploadError(metadata, e);
        }
        return s3ObjectKey;
    }

    @NotNull
    @Override
    public InputStream downloadFile(@NotBlank String s3ObjectKey) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(s3ObjectKey)
                            .build()
            );
        } catch (Exception e) {
            throw new UnexpectedException(
                    "Cannot download file with object key [%s]".formatted(s3ObjectKey),
                    e
            );
        }
    }

    @NotNull
    @Override
    public InputStream downloadFileChunk(@NotBlank String s3ObjectKey, @NotNull ContentRangeSelection rangeSelection) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .offset(rangeSelection.getStartByte())
                            .length(rangeSelection.getContentLength())
                            .bucket(bucketName)
                            .object(s3ObjectKey)
                            .build()
            );
        } catch (Exception e) {
            throw new UnexpectedException(
                    "Cannot download file chunk [%s] with object key [%s]".formatted(rangeSelection, s3ObjectKey),
                    e
            );
        }
    }

    @Override
    public void deleteFile(@NotBlank String s3ObjectKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(s3ObjectKey)
                            .build()
            );
        } catch (Exception e) {
            throw new UnexpectedException(
                    "Cannot delete uploaded file with object key [%s]".formatted(s3ObjectKey),
                    e
            );
        }
    }

    private void ensureBucketExists() {
        try {
            if (!minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            )) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
            }
        } catch (Exception e) {
            throw new UnexpectedException("Cannot check S3-bucket [%s] existence".formatted(bucketName), e);
        }
    }

    private void throwUnexpectedUploadError(Map<String, String> metadata, Exception e) {
        throw new UnexpectedException(
                "Cannot upload file [%s.%s] to bucket [%s]".formatted(
                        metadata.get(S3FileStorage.FILENAME_META),
                        metadata.get(S3FileStorage.FILE_EXT_META),
                        bucketName
                ),
                e
        );
    }

}
