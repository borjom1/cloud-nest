package com.cloud.nest.fm.persistence.s3;

import com.cloud.nest.fm.config.MinIOProperties;
import com.cloud.nest.platform.model.exception.UnexpectedException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Component
public class MinIOS3FileStorage implements S3FileStorage {

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
        final String filename = metadata.get(S3FileStorage.FILENAME_META);
        final String ext = metadata.get(S3FileStorage.FILE_EXT_META);
        final String s3ObjectKey = objectKeyCreator.getObjectKey(userId, ext);

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(s3ObjectKey)
                            .contentType(file.getContentType())
                            .userMetadata(metadata)
                            .stream(file.getInputStream(), file.getSize(), partSize)
                            .build()
            );
            return s3ObjectKey;
        } catch (Exception e) {
            throw new UnexpectedException(
                    "Cannot upload file [%s.%s] to bucket [%s]".formatted(filename, ext, bucketName),
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

}
